package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.enums.FuelEfficiencyStatus;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.models.VehicleStateCurrentModel;
import com.icm.telemetria_peru_api.repositories.VehicleStateCurrentRepository;
import com.icm.telemetria_peru_api.services.FuelEfficiencyService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FuelEfficiencyDailyHandler {

    private final FuelEfficiencyService fuelEfficiencyService;
    private final VehicleStateCurrentRepository vehicleStateCurrentRepository;

    @PersistenceContext
    private EntityManager em;

    private static final ZoneId ZONE = ZoneId.of("America/Lima");
    private static final long ENGINE_VOLTAGE_MV = 27000;
    private static final long DUPLICATE_TOLERANCE_SECONDS = 0;

    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 5)
    public void process(Long vehicleId, VehiclePayloadMqttDTO p) {
        if (vehicleId == null || p == null) return;

        ZonedDateTime eventTime = parseEventTime(p);
        FuelEfficiencyStatus newStatus = determinateStatus(p);

        VehicleStateCurrentModel current = vehicleStateCurrentRepository
                .findByVehicleModel_Id(vehicleId)
                .orElse(null);

        if (current == null) {
            VehicleStateCurrentModel init = new VehicleStateCurrentModel();

            // asigna relación correctamente (proxy, sin SELECT)
            VehicleModel vehicleRef = em.getReference(VehicleModel.class, vehicleId);
            init.setVehicleModel(vehicleRef);

            init.setStatus(newStatus);
            init.setLastEventTime(eventTime);

            vehicleStateCurrentRepository.save(init);
            return;
        }

        ZonedDateTime lastTime = current.getLastEventTime();
        if (lastTime == null) {
            current.setStatus(newStatus);
            current.setLastEventTime(eventTime);
            vehicleStateCurrentRepository.save(current);
            return;
        }

        if (eventTime.isBefore(lastTime)) return;

        long deltaSeconds = Duration.between(lastTime, eventTime).getSeconds();
        if (deltaSeconds <= DUPLICATE_TOLERANCE_SECONDS) return;

        FuelEfficiencyStatus prevStatus = current.getStatus();
        accumulateSplitByDay(vehicleId, lastTime, eventTime, prevStatus);

        current.setStatus(newStatus);
        current.setLastEventTime(eventTime);
        vehicleStateCurrentRepository.save(current);
    }

    private ZonedDateTime parseEventTime(VehiclePayloadMqttDTO p) {
        if (p.getTimestamp() == null) return ZonedDateTime.now(ZONE);

        long ts;
        try {
            ts = Long.parseLong(String.valueOf(p.getTimestamp()));
        } catch (Exception e) {
            return ZonedDateTime.now(ZONE);
        }

        // seconds -> ms
        if (ts < 1_000_000_000_000L) ts *= 1000L;

        return Instant.ofEpochMilli(ts).atZone(ZONE);
    }

    private FuelEfficiencyStatus determinateStatus(VehiclePayloadMqttDTO p) {
        Boolean ignition = p.getIgnitionInfo();
        if (ignition == null || !ignition) return FuelEfficiencyStatus.ESTACIONADO;

        boolean moving = isMoving(p);
        Long extV = getExternalVoltageMv(p);
        boolean voltageHigh = (extV == null) || (extV >= ENGINE_VOLTAGE_MV);

        if (moving && voltageHigh) return FuelEfficiencyStatus.OPERACION;
        return FuelEfficiencyStatus.RALENTI;
    }

    private boolean isMoving(VehiclePayloadMqttDTO p) {
        Integer movement = p.getMovement();
        Integer instant = p.getInstantMovement();
        if (movement != null && movement == 1) return true;
        if (instant != null && instant == 1) return true;

        Integer io37 = p.getVehicleSpeedIo();
        if (io37 != null && io37 > 0) return true;

        Double gps = p.getSpeed();
        return gps != null && gps > 0.0;
    }

    private Long getExternalVoltageMv(VehiclePayloadMqttDTO p) {
        Integer v = p.getExternalVoltage();
        return v == null ? null : v.longValue();
    }

    private void accumulateSplitByDay(Long vehicleId, ZonedDateTime from, ZonedDateTime to, FuelEfficiencyStatus status) {
        ZonedDateTime cursor = from;

        while (cursor.toLocalDate().isBefore(to.toLocalDate())) {
            ZonedDateTime endOfDay = cursor.toLocalDate().plusDays(1).atStartOfDay(ZONE);
            long seconds = Duration.between(cursor, endOfDay).getSeconds();
            if (seconds > 0) addToDaily(vehicleId, cursor.toLocalDate(), status, seconds);
            cursor = endOfDay;
        }

        long secondsLast = Duration.between(cursor, to).getSeconds();
        if (secondsLast > 0) addToDaily(vehicleId, cursor.toLocalDate(), status, secondsLast);
    }

    private void addToDaily(Long vehicleId, LocalDate day, FuelEfficiencyStatus status, long seconds) {
        long parked = 0, idle = 0, op = 0;

        switch (status) {
            case ESTACIONADO -> parked = seconds;
            case RALENTI -> idle = seconds;
            case OPERACION -> op = seconds;
        }

        fuelEfficiencyService.addSeconds(vehicleId, day, parked, idle, op);
    }
}
