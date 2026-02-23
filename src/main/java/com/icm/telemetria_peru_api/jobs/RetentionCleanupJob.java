package com.icm.telemetria_peru_api.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * RetentionCleanupJob
 * -------------------
 * Aplica políticas de retención sobre datos de telemetría en DB.
 *
 * Características:
 * - Dry-run para auditoría (no borra, solo reporta)
 * - Borrado por batches para evitar locks largos
 * - Logging detallado y métricas básicas
 *
 * Ajusta tabla/columna según tu esquema real.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RetentionCleanupJob {

    private final DataSource dataSource;

    @Value("${jobs.retention.enabled:true}")
    private boolean enabled;

    @Value("${jobs.retention.zone:America/Lima}")
    private String zone;

    @Value("${jobs.retention.retention-days:90}")
    private int retentionDays;

    @Value("${jobs.retention.batch-size:5000}")
    private int batchSize;

    @Value("${jobs.retention.max-batches:50}")
    private int maxBatches;

    @Value("${jobs.retention.dry-run:true}")
    private boolean dryRun;

    /**
     * Corre diario a las 03:15 AM (Lima), típico horario de mantenimiento.
     */
    @Scheduled(cron = "0 15 3 * * *", zone = "America/Lima")
    public void run() {
        if (!enabled) {
            log.debug("[RETENTION] disabled");
            return;
        }

        ZoneId zid = ZoneId.of(zone);
        ZonedDateTime now = ZonedDateTime.now(zid);
        ZonedDateTime cutoff = now.minusDays(retentionDays);

        long startedAt = System.currentTimeMillis();

        try (Connection cx = dataSource.getConnection()) {
            // 1) Estimar cuántos registros serían elegibles
            long eligible = countEligible(cx, cutoff);

            if (eligible <= 0) {
                log.info("[RETENTION] nothing to cleanup cutoff={} retentionDays={} dryRun={}", cutoff, retentionDays, dryRun);
                return;
            }

            log.info("[RETENTION] start eligible={} cutoff={} retentionDays={} batchSize={} maxBatches={} dryRun={}",
                    eligible, cutoff, retentionDays, batchSize, maxBatches, dryRun);

            if (dryRun) {
                // Solo reporta; útil para “primera semana” sin riesgo
                long tookMs = System.currentTimeMillis() - startedAt;
                log.warn("[RETENTION] DRY_RUN wouldDelete={} cutoff={} tookMs={}", eligible, cutoff, tookMs);
                return;
            }

            // 2) Borrar por lotes
            long deletedTotal = 0;
            int batches = 0;

            while (batches < maxBatches) {
                long deleted = deleteBatch(cx, cutoff, batchSize);
                batches++;

                deletedTotal += deleted;

                log.info("[RETENTION] batch={} deleted={} deletedTotal={}", batches, deleted, deletedTotal);

                // Si ya no borró nada, terminó
                if (deleted <= 0) break;
            }

            long remaining = countEligible(cx, cutoff);

            long tookMs = System.currentTimeMillis() - startedAt;

            log.info("[RETENTION] done deletedTotal={} batches={} remainingEligible={} cutoff={} tookMs={}",
                    deletedTotal, batches, remaining, cutoff, tookMs);

        } catch (Exception e) {
            log.error("[RETENTION] fatal error msg={}", e.getMessage(), e);
        }
    }

    /**
     * Ajusta "position_records" y "event_time" a tu esquema.
     */
    private long countEligible(Connection cx, ZonedDateTime cutoff) throws Exception {
        String sql = """
            SELECT COUNT(1) AS cnt
            FROM position_records
            WHERE event_time < ?
        """;
        try (PreparedStatement ps = cx.prepareStatement(sql)) {
            ps.setTimestamp(1, java.sql.Timestamp.from(cutoff.toInstant()));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong("cnt");
            }
        }
    }

    /**
     * Borra un batch. Implementación depende del motor:
     * - En MySQL/Postgres puedes usar LIMIT en DELETE (ojo Postgres requiere USING/CTE).
     *
     * Aquí doy una forma genérica usando un subselect por IDs (común y segura).
     */
    private long deleteBatch(Connection cx, ZonedDateTime cutoff, int limit) throws Exception {
        // IMPORTANTE: asumo que position_records tiene PK "id".
        // Ajusta PK y tabla si es diferente.
        String sql = """
            DELETE FROM position_records
            WHERE id IN (
                SELECT id
                FROM position_records
                WHERE event_time < ?
                ORDER BY event_time ASC
                LIMIT ?
            )
        """;
        try (PreparedStatement ps = cx.prepareStatement(sql)) {
            ps.setTimestamp(1, java.sql.Timestamp.from(cutoff.toInstant()));
            ps.setInt(2, limit);
            return ps.executeUpdate();
        }
    }
}