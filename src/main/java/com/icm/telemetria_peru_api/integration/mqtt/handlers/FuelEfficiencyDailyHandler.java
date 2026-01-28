package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.enums.FuelEfficiencyStatus;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.models.VehicleStateCurrentModel;
import com.icm.telemetria_peru_api.repositories.VehicleStateCurrentRepository;
import com.icm.telemetria_peru_api.services.FuelEfficiencyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FuelEfficiencyDailyHandler {

    private final FuelEfficiencyService fuelEfficiencyService;
    private final VehicleStateCurrentRepository vehicleStateCurrentRepository;

    private static final ZoneId ZONE = ZoneId.of("America/Lima");

    // Teltonika IO 66 suele venir en mV (26176 => 26.176V)
    private static final long ENGINE_VOLTAGE_MV = 27000;

    // Evita duplicados: si el tracker manda el mismo segundo muchas veces, no acumules nada.
    private static final long DUPLICATE_TOLERANCE_SECONDS = 0; // 0 = exacto; puedes subir a 1 si tu tracker repite mucho

    @Transactional
    public void process(VehicleModel vehicle, VehiclePayloadMqttDTO p) {
        if (vehicle == null || p == null) return;

        ZonedDateTime eventTime = parseEventTime(p);
        FuelEfficiencyStatus newStatus = determinateStatus(p);

        VehicleStateCurrentModel current = vehicleStateCurrentRepository
                .findByVehicleModel_Id(vehicle.getId())
                .orElse(null);

        // Primer evento: inicializa estado (sin acumular)
        if (current == null) {
            VehicleStateCurrentModel init = new VehicleStateCurrentModel();
            init.setVehicleModel(vehicle);
            init.setStatus(newStatus);
            init.setLastEventTime(eventTime);
            vehicleStateCurrentRepository.save(init);

            log.debug("[FE] init vehicle={} eventTime={} status={}", vehicle.getId(), eventTime, newStatus);
            return;
        }

        ZonedDateTime lastTime = current.getLastEventTime();
        if (lastTime == null) {
            current.setStatus(newStatus);
            current.setLastEventTime(eventTime);
            vehicleStateCurrentRepository.save(current);
            return;
        }

        // Si llega desordenado (menor), ignora
        if (eventTime.isBefore(lastTime)) {
            log.debug("[FE] ignore out-of-order vehicle={} last={} event={}", vehicle.getId(), lastTime, eventTime);
            return;
        }

        long deltaSeconds = Duration.between(lastTime, eventTime).getSeconds();

        // Duplicado / mismo segundo
        if (deltaSeconds <= DUPLICATE_TOLERANCE_SECONDS) {
            // (opcional) si quieres, solo actualiza status sin tocar tiempo:
            // current.setStatus(newStatus); vehicleStateCurrentRepository.save(current);
            return;
        }

        FuelEfficiencyStatus prevStatus = current.getStatus();

        // Acumula el delta en el estado anterior
        accumulateSplitByDay(vehicle.getId(), lastTime, eventTime, prevStatus);

        // Actualiza estado + lastEventTime
        current.setStatus(newStatus);
        current.setLastEventTime(eventTime);
        vehicleStateCurrentRepository.save(current);

        log.debug("[FE] acc vehicle={} prev={} new={} delta={}s from={} to={}",
                vehicle.getId(), prevStatus, newStatus, deltaSeconds, lastTime, eventTime);
    }

    private ZonedDateTime parseEventTime(VehiclePayloadMqttDTO p) {
        // fallback MUY conservador (no inventa acumulación)
        if (p.getTimestamp() == null) {
            return ZonedDateTime.now(ZONE);
        }

        long ts;
        try {
            ts = Long.parseLong(String.valueOf(p.getTimestamp()));
        } catch (Exception e) {
            // Si viene raro, usa now() pero ojo que puede alterar continuidad.
            return ZonedDateTime.now(ZONE);
        }

        // Si viene en segundos, pásalo a ms
        if (ts < 1_000_000_000_000L) {
            ts *= 1000L;
        }

        return Instant.ofEpochMilli(ts).atZone(ZONE);
    }

    private FuelEfficiencyStatus determinateStatus(VehiclePayloadMqttDTO p) {
        Boolean ignition = p.getIgnitionInfo(); // ya convertido correctamente (ver fix abajo)
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

        Integer vehicleSpeedIo = p.getVehicleSpeedIo();
        if (vehicleSpeedIo != null && vehicleSpeedIo > 0) return true;

        Double speed = p.getSpeed();
        return speed != null && speed > 0.0;
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
