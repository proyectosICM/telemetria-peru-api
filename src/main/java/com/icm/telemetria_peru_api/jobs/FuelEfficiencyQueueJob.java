package com.icm.telemetria_peru_api.jobs;

import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.integration.mqtt.handlers.FuelEfficiencyDailyHandler;
import com.icm.telemetria_peru_api.models.FuelEfficiencyQueueModel;
import com.icm.telemetria_peru_api.services.FuelEfficiencyQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FuelEfficiencyQueueJob {

    private final FuelEfficiencyQueueService fuelEfficiencyQueueService;
    private final FuelEfficiencyDailyHandler fuelEfficiencyDailyHandler;

    private static final int BATCH_SIZE = 100;

    @Scheduled(fixedDelayString = "PT5S", initialDelayString = "PT20S")
    public void run() {
        long startMs = System.currentTimeMillis();

        List<FuelEfficiencyQueueModel> batch = fuelEfficiencyQueueService.takePendingBatch(BATCH_SIZE);

        if (batch.isEmpty()) {
            log.debug("[FUEL_EFFICIENCY_QUEUE_JOB] no pending items");
            return;
        }

        int ok = 0;
        int fail = 0;

        log.info("[FUEL_EFFICIENCY_QUEUE_JOB] start batchSize={}", batch.size());

        for (FuelEfficiencyQueueModel item : batch) {
            try {
                VehiclePayloadMqttDTO dto = mapToDto(item);

                fuelEfficiencyDailyHandler.process(item.getVehicleModel().getId(), dto);
                fuelEfficiencyQueueService.markProcessed(item.getId());
                ok++;

            } catch (Exception e) {
                fail++;
                fuelEfficiencyQueueService.markFailed(item.getId(), e.getMessage());

                log.error("[FUEL_EFFICIENCY_QUEUE_JOB] error queueId={} vehicleId={} msg={}",
                        item.getId(),
                        item.getVehicleModel().getId(),
                        e.getMessage(),
                        e
                );
            }
        }

        long tookMs = System.currentTimeMillis() - startMs;

        log.info("[FUEL_EFFICIENCY_QUEUE_JOB] done ok={} fail={} total={} tookMs={}",
                ok, fail, batch.size(), tookMs);
    }

    @Scheduled(fixedDelayString = "PT10M", initialDelayString = "PT2M")
    public void releaseStuckItems() {
        fuelEfficiencyQueueService.releaseStuckProcessingItems();
    }

    private VehiclePayloadMqttDTO mapToDto(FuelEfficiencyQueueModel item) {
        VehiclePayloadMqttDTO dto = new VehiclePayloadMqttDTO();

        dto.setVehicleId(item.getVehicleModel().getId());
        dto.setTimestamp(String.valueOf(item.getEventTime().toInstant().toEpochMilli()));
        dto.setIgnitionInfo(item.getIgnitionInfo());
        dto.setMovement(item.getMovement());
        dto.setInstantMovement(item.getInstantMovement());
        dto.setVehicleSpeedIo(item.getVehicleSpeedIo());
        dto.setSpeed(item.getSpeed());
        dto.setExternalVoltage(item.getExternalVoltage());

        return dto;
    }
}
