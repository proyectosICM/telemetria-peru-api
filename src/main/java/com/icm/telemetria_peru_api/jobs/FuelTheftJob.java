package com.icm.telemetria_peru_api.jobs;

import com.icm.telemetria_peru_api.repositories.FuelRecordRepository;
import com.icm.telemetria_peru_api.services.FuelTheftDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FuelTheftJob {

    private final FuelRecordRepository fuelRecordRepository;
    private final FuelTheftDetectionService detectionService;
    private final FuelTheftJobConfig cfg;

    // Corre cada 1 minuto (igual que tu ejemplo), pero mejor controlado
    @Scheduled(fixedDelayString = "PT1M", initialDelayString = "PT30S")
    public void run() {
        if (!cfg.enabled()) return;

        ZoneId zone = ZoneId.of(cfg.zone());
        ZonedDateTime now = ZonedDateTime.now(zone);
        ZonedDateTime since = now.minusMinutes(cfg.activeWindowMinutes());

        long startMs = System.currentTimeMillis();

        // Idealmente el repo devuelve ya limitado por batchSize
        List<Long> vehicleIds = fuelRecordRepository.findActiveVehicleIdsSinceLimited(since, cfg.batchSize());

        if (vehicleIds.isEmpty()) {
            log.debug("[FUEL_THEFT_JOB] no active vehicles since={} zone={}", since, cfg.zone());
            return;
        }

        int ok = 0;
        int fail = 0;

        log.info("[FUEL_THEFT_JOB] start vehicles={} since={} windowMin={} zone={}",
                vehicleIds.size(), since, cfg.activeWindowMinutes(), cfg.zone());

        for (Long vehicleId : vehicleIds) {
            try {
                detectionService.analyzeVehicle(vehicleId);
                ok++;
            } catch (Exception e) {
                fail++;
                log.error("[FUEL_THEFT_JOB] error vehicleId={} msg={}", vehicleId, e.getMessage(), e);
            }
        }

        long tookMs = System.currentTimeMillis() - startMs;

        log.info("[FUEL_THEFT_JOB] done ok={} fail={} vehicles={} tookMs={}",
                ok, fail, vehicleIds.size(), tookMs);
    }
}