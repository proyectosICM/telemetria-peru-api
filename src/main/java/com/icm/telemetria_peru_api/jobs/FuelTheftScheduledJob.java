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
public class FuelTheftScheduledJob {
/*
    private final FuelRecordRepository fuelRecordRepository;
    private final FuelTheftDetectionService detectionService;

    private static final ZoneId ZONE = ZoneId.of("America/Lima");

    // analiza solo vehículos con actividad en los últimos X minutos
    private static final int ACTIVE_WINDOW_MINUTES = 10;

    // Corre cada 1 minuto (ajusta)
    @Scheduled(fixedDelay = 60_000, initialDelay = 30_000)
    public void run() {
        ZonedDateTime since = ZonedDateTime.now(ZONE).minusMinutes(ACTIVE_WINDOW_MINUTES);

        List<Long> activeVehicleIds = fuelRecordRepository.findActiveVehicleIdsSince(since);

        if (activeVehicleIds.isEmpty()) return;

        log.debug("[FUEL_THEFT_JOB] activeVehicles={}", activeVehicleIds.size());

        // Si tienes muchos, puedes limitar aquí o paginar
        for (Long vehicleId : activeVehicleIds) {
            try {
                detectionService.analyzeVehicle(vehicleId);
            } catch (Exception e) {
                log.error("[FUEL_THEFT_JOB] error vehicleId={} msg={}", vehicleId, e.getMessage(), e);
            }
        }
    }*/
}
