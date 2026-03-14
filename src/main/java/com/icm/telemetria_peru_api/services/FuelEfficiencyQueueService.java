package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.models.FuelEfficiencyQueueModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.FuelEfficiencyQueueRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FuelEfficiencyQueueService {

    private final FuelEfficiencyQueueRepository fuelEfficiencyQueueRepository;

    @PersistenceContext
    private EntityManager em;

    private static final ZoneId ZONE = ZoneId.of("America/Lima");

    @Transactional
    public void enqueue(Long vehicleId, VehiclePayloadMqttDTO p) {
        if (vehicleId == null || p == null) return;

        FuelEfficiencyQueueModel item = new FuelEfficiencyQueueModel();

        VehicleModel vehicleRef = em.getReference(VehicleModel.class, vehicleId);
        item.setVehicleModel(vehicleRef);

        item.setEventTime(parseEventTime(p));
        item.setIgnitionInfo(p.getIgnitionInfo());
        item.setMovement(p.getMovement());
        item.setInstantMovement(p.getInstantMovement());
        item.setVehicleSpeedIo(p.getVehicleSpeedIo());
        item.setSpeed(p.getSpeed());
        item.setExternalVoltage(p.getExternalVoltage());

        item.setProcessing(false);
        item.setProcessed(false);
        item.setAttemptCount(0);
        item.setProcessedAt(null);
        item.setLastError(null);

        fuelEfficiencyQueueRepository.save(item);
    }

    @Transactional
    public List<FuelEfficiencyQueueModel> takePendingBatch(int batchSize) {
        List<FuelEfficiencyQueueModel> pending = fuelEfficiencyQueueRepository
                .findTop100ByProcessedFalseAndProcessingFalseOrderByEventTimeAscCreatedAtAsc();

        if (pending.isEmpty()) return pending;

        List<FuelEfficiencyQueueModel> result = new ArrayList<>();

        int limit = Math.min(batchSize, pending.size());

        for (int i = 0; i < limit; i++) {
            FuelEfficiencyQueueModel item = pending.get(i);
            item.setProcessing(true);
            fuelEfficiencyQueueRepository.save(item);
            result.add(item);
        }

        return result;
    }

    @Transactional
    public void markProcessed(Long queueId) {
        fuelEfficiencyQueueRepository.findById(queueId).ifPresent(item -> {
            item.setProcessing(false);
            item.setProcessed(true);
            item.setProcessedAt(ZonedDateTime.now(ZONE));
            item.setLastError(null);
            fuelEfficiencyQueueRepository.save(item);
        });
    }

    @Transactional
    public void markFailed(Long queueId, String errorMessage) {
        fuelEfficiencyQueueRepository.findById(queueId).ifPresent(item -> {
            item.setProcessing(false);
            item.setAttemptCount(item.getAttemptCount() + 1);
            item.setLastError(truncate(errorMessage, 500));
            fuelEfficiencyQueueRepository.save(item);
        });
    }

    @Transactional(readOnly = true)
    public long countPending() {
        return fuelEfficiencyQueueRepository.countByProcessedFalseAndProcessingFalse();
    }

    @Transactional
    public void releaseStuckProcessingItems() {
        List<FuelEfficiencyQueueModel> stuckItems = fuelEfficiencyQueueRepository
                .findByProcessedFalseAndProcessingTrueOrderByEventTimeAscCreatedAtAsc();

        if (stuckItems.isEmpty()) return;

        for (FuelEfficiencyQueueModel item : stuckItems) {
            item.setProcessing(false);
            fuelEfficiencyQueueRepository.save(item);
        }

        log.warn("[FUEL_EFFICIENCY_QUEUE] released stuck items={}", stuckItems.size());
    }

    @Transactional
    public void deleteProcessedItems() {
        fuelEfficiencyQueueRepository.deleteByProcessedTrue();
    }

    private ZonedDateTime parseEventTime(VehiclePayloadMqttDTO p) {
        if (p.getTimestamp() == null) return ZonedDateTime.now(ZONE);

        long ts;
        try {
            ts = Long.parseLong(String.valueOf(p.getTimestamp()));
        } catch (Exception e) {
            return ZonedDateTime.now(ZONE);
        }

        if (ts < 1_000_000_000_000L) ts *= 1000L;

        return Instant.ofEpochMilli(ts).atZone(ZONE);
    }

    private String truncate(String value, int maxLength) {
        if (value == null) return null;
        if (value.length() <= maxLength) return value;
        return value.substring(0, maxLength);
    }
}
