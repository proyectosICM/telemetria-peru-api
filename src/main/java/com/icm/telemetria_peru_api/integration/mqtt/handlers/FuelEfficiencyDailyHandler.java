package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.enums.FuelEfficiencyStatus;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.models.VehicleStateCurrentModel;
import com.icm.telemetria_peru_api.repositories.VehicleStateCurrentRepository;
import com.icm.telemetria_peru_api.services.FuelEfficiencyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.*;

@Component
@RequiredArgsConstructor
public class FuelEfficiencyDailyHandler {

    private final FuelEfficiencyService fuelEfficiencyService;
    private final VehicleStateCurrentRepository vehicleStateCurrentRepository;

    private static final ZoneId ZONE = ZoneId.of("America/Lima");

    // Teltonika IO 66 suele venir en mV (26176 => 26.176V)
    private static final long ENGINE_VOLTAGE_MV = 27000;

    // Si el tracker manda timestamps repetidos, usamos "now" para acumular (tolerancia)
    private static final boolean ALLOW_FALLBACK_NOW_WHEN_TS_REPEATED = true;

    @Transactional
    public void process(VehicleModel vehicle, VehiclePayloadMqttDTO p) {

        ZonedDateTime eventTime = parseEventTime(p);
        FuelEfficiencyStatus newStatus = determinateStatus(p);

        VehicleStateCurrentModel current = vehicleStateCurrentRepository
                .findByVehicleModel_Id(vehicle.getId())
                .orElse(null);

        if (current == null) {
            VehicleStateCurrentModel init = new VehicleStateCurrentModel();
            init.setVehicleModel(vehicle);
            init.setStatus(newStatus);
            init.setLastEventTime(eventTime);
            vehicleStateCurrentRepository.save(init);

            // DEBUG
            System.out.println("[FE] INIT vehicle=" + vehicle.getId()
                    + " ts=" + p.getTimestamp()
                    + " eventTime=" + eventTime
                    + " status=" + newStatus);

            return;
        }

        ZonedDateTime lastTime = current.getLastEventTime();
        ZonedDateTime effectiveEventTime = eventTime;

        // Si llega repetido o desordenado, es muy probable que el tracker repita timestamp.
        // Para no quedarnos sin acumular, podemos usar now() como "tiempo de llegada".
        if (!eventTime.isAfter(lastTime)) {
            if (!ALLOW_FALLBACK_NOW_WHEN_TS_REPEATED) {
                System.out.println("[FE] IGNORE vehicle=" + vehicle.getId()
                        + " ts=" + p.getTimestamp()
                        + " eventTime=" + eventTime
                        + " lastTime=" + lastTime
                        + " reason=eventTime<=lastTime");
                return;
            }

            ZonedDateTime now = ZonedDateTime.now(ZONE);
            if (!now.isAfter(lastTime)) {
                System.out.println("[FE] IGNORE vehicle=" + vehicle.getId()
                        + " ts=" + p.getTimestamp()
                        + " eventTime=" + eventTime
                        + " lastTime=" + lastTime
                        + " now=" + now
                        + " reason=now<=lastTime");
                return;
            }

            effectiveEventTime = now;

            System.out.println("[FE] FALLBACK_NOW vehicle=" + vehicle.getId()
                    + " ts=" + p.getTimestamp()
                    + " eventTime=" + eventTime
                    + " lastTime=" + lastTime
                    + " effectiveEventTime=" + effectiveEventTime);
        }

        long deltaSeconds = Duration.between(lastTime, effectiveEventTime).getSeconds();
        if (deltaSeconds <= 0) {
            System.out.println("[FE] IGNORE vehicle=" + vehicle.getId()
                    + " ts=" + p.getTimestamp()
                    + " lastTime=" + lastTime
                    + " effectiveEventTime=" + effectiveEventTime
                    + " deltaSeconds=" + deltaSeconds
                    + " reason=deltaSeconds<=0");
            return;
        }

        FuelEfficiencyStatus prevStatus = current.getStatus();

        // DEBUG
        System.out.println("[FE] ACC vehicle=" + vehicle.getId()
                + " prevStatus=" + prevStatus
                + " newStatus=" + newStatus
                + " lastTime=" + lastTime
                + " effectiveEventTime=" + effectiveEventTime
                + " deltaSeconds=" + deltaSeconds
                + " day(from)=" + lastTime.toLocalDate()
                + " day(to)=" + effectiveEventTime.toLocalDate());

        accumulateSplitByDay(vehicle.getId(), lastTime, effectiveEventTime, prevStatus);

        // Actualiza estado actual (guardamos el tiempo “efectivo” para mantener continuidad)
        current.setStatus(newStatus);
        current.setLastEventTime(effectiveEventTime);
        vehicleStateCurrentRepository.save(current);
    }

    private ZonedDateTime parseEventTime(VehiclePayloadMqttDTO p) {
        if (p == null || p.getTimestamp() == null) {
            return ZonedDateTime.now(ZONE);
        }

        long ts = Long.parseLong(String.valueOf(p.getTimestamp()));

        // seconds -> ms
        if (ts < 1_000_000_000_000L) {
            ts = ts * 1000L;
        }

        return Instant.ofEpochMilli(ts).atZone(ZONE);
    }

    private FuelEfficiencyStatus determinateStatus(VehiclePayloadMqttDTO p) {
        Boolean ignition = p.getIgnitionInfo();
        if (ignition == null || !ignition) {
            return FuelEfficiencyStatus.ESTACIONADO;
        }

        boolean moving = isMoving(p);
        Long extV = getExternalVoltageMv(p);

        boolean voltageHigh = (extV == null) || (extV >= ENGINE_VOLTAGE_MV);

        if (moving && voltageHigh) {
            return FuelEfficiencyStatus.OPERACION;
        }

        return FuelEfficiencyStatus.RALENTI;
    }

    private boolean isMoving(VehiclePayloadMqttDTO p) {
        Integer movement = p.getMovement();
        Integer instant = p.getInstantMovement();

        if (movement != null && movement == 1) return true;
        if (instant != null && instant == 1) return true;

        Double speed = p.getSpeed();
        if (speed != null && speed > 0.0) return true;

        Integer vehicleSpeedIo = p.getVehicleSpeedIo();
        return vehicleSpeedIo != null && vehicleSpeedIo > 0;
    }

    private Long getExternalVoltageMv(VehiclePayloadMqttDTO p) {
        if (p.getExternalVoltage() == null) return null;
        return Long.valueOf(p.getExternalVoltage());
    }

    private void accumulateSplitByDay(Long vehicleId, ZonedDateTime from, ZonedDateTime to, FuelEfficiencyStatus status) {
        ZonedDateTime cursor = from;

        while (cursor.toLocalDate().isBefore(to.toLocalDate())) {
            ZonedDateTime endOfDay = cursor.toLocalDate().plusDays(1).atStartOfDay(ZONE);
            long seconds = Duration.between(cursor, endOfDay).getSeconds();
            addToDaily(vehicleId, cursor.toLocalDate(), status, seconds);
            cursor = endOfDay;
        }

        long secondsLast = Duration.between(cursor, to).getSeconds();
        if (secondsLast > 0) {
            addToDaily(vehicleId, cursor.toLocalDate(), status, secondsLast);
        }
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
