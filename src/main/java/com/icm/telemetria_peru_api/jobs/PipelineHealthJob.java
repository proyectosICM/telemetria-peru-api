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
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * PipelineHealthJob
 * -----------------
 * Job de salud del pipeline (ingestión/DB) para telemetría:
 * - mide si están entrando registros
 * - calcula lag desde último evento
 * - cuenta vehículos activos por ventana
 * - loguea un "health summary" útil para auditoría/operación
 *
 * Importante: no modifica datos. Solo lectura.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PipelineHealthJob {

    private final DataSource dataSource;

    @Value("${jobs.pipeline-health.enabled:true}")
    private boolean enabled;

    @Value("${jobs.pipeline-health.zone:America/Lima}")
    private String zone;

    @Value("${jobs.pipeline-health.active-window-minutes:60}")
    private int activeWindowMinutes;

    @Value("${jobs.pipeline-health.max-acceptable-lag-minutes:15}")
    private int maxAcceptableLagMinutes;

    /**
     * Corre todos los días a las 10:00 AM (hora local de la JVM).
     * Si quieres forzar Lima independientemente de la JVM: usa zone="America/Lima".
     */
    @Scheduled(cron = "0 0 10 * * *", zone = "America/Lima")
    public void run() {
        if (!enabled) {
            log.debug("[PIPELINE_HEALTH] disabled");
            return;
        }

        ZoneId zid = ZoneId.of(zone);
        ZonedDateTime now = ZonedDateTime.now(zid);
        ZonedDateTime since = now.minusMinutes(activeWindowMinutes);

        long startedAt = System.currentTimeMillis();

        try (Connection cx = dataSource.getConnection()) {
            // 1) Último timestamp de evento
            ZonedDateTime lastEventAt = queryLastEventTime(cx);

            // 2) Lag desde último evento
            Long lagMinutes = lastEventAt == null ? null : Duration.between(lastEventAt, now).toMinutes();

            // 3) Conteos por ventanas
            long lastHourCount = queryCountSince(cx, now.minusHours(1));
            long last24hCount = queryCountSince(cx, now.minusHours(24));

            // 4) Vehículos activos (con data en la ventana)
            long activeVehicles = queryActiveVehiclesSince(cx, since);

            PipelineStatus status = evaluateStatus(lastHourCount, lagMinutes);

            long tookMs = System.currentTimeMillis() - startedAt;

            // Log “bonito” y defendible en operación
            logAtLevel(status,
                    "[PIPELINE_HEALTH] status={} now={} lastEventAt={} lagMin={} activeWindowMin={} activeVehicles={} lastHourCount={} last24hCount={} tookMs={}",
                    status.name(),
                    now,
                    lastEventAt,
                    lagMinutes,
                    activeWindowMinutes,
                    activeVehicles,
                    lastHourCount,
                    last24hCount,
                    tookMs
            );

        } catch (Exception e) {
            log.error("[PIPELINE_HEALTH] fatal error msg={}", e.getMessage(), e);
        }
    }

    private PipelineStatus evaluateStatus(long lastHourCount, Long lagMinutes) {
        // Criterios simples pero útiles:
        // - si no entra nada en 1h => ERROR (pipeline caído o inactividad total)
        // - si hay lag demasiado alto => WARN (posible caída parcial / dispositivos offline)
        if (lastHourCount <= 0) return PipelineStatus.ERROR;
        if (lagMinutes != null && lagMinutes > maxAcceptableLagMinutes) return PipelineStatus.WARN;
        return PipelineStatus.OK;
    }

    private void logAtLevel(PipelineStatus status, String msg, Object... args) {
        switch (status) {
            case OK -> log.info(msg, args);
            case WARN -> log.warn(msg, args);
            case ERROR -> log.error(msg, args);
        }
    }

    /**
     * OJO: Ajusta nombres de tabla/columna según tu schema real.
     * Aquí uso una tabla genérica "position_records" con columna "event_time".
     * Si tu plataforma usa otra (gps_records, positions, telemetry_packets), cambia acá.
     */
    private ZonedDateTime queryLastEventTime(Connection cx) throws Exception {
        String sql = """
            SELECT MAX(event_time) AS last_time
            FROM position_records
        """;
        try (PreparedStatement ps = cx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) return null;
            var ts = rs.getTimestamp("last_time");
            return ts == null ? null : ts.toInstant().atZone(ZoneId.of(zone));
        }
    }

    private long queryCountSince(Connection cx, ZonedDateTime since) throws Exception {
        String sql = """
            SELECT COUNT(1) AS cnt
            FROM position_records
            WHERE event_time >= ?
        """;
        try (PreparedStatement ps = cx.prepareStatement(sql)) {
            ps.setTimestamp(1, java.sql.Timestamp.from(since.toInstant()));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong("cnt");
            }
        }
    }

    private long queryActiveVehiclesSince(Connection cx, ZonedDateTime since) throws Exception {
        String sql = """
            SELECT COUNT(DISTINCT vehicle_id) AS cnt
            FROM position_records
            WHERE event_time >= ?
        """;
        try (PreparedStatement ps = cx.prepareStatement(sql)) {
            ps.setTimestamp(1, java.sql.Timestamp.from(since.toInstant()));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong("cnt");
            }
        }
    }

    private enum PipelineStatus {
        OK, WARN, ERROR
    }
}
/* Prueba */