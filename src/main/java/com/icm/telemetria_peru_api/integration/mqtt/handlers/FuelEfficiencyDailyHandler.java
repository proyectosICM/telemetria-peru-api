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

    // Negocio: America/Lima
    private static final ZoneId ZONE = ZoneId.of("America/Lima");

    // Umbral típico para alternador cargando (mV). Ajusta a tu realidad.
    // OJO: en muchos Teltonika, 66 viene en mV (ej: 26176 = 26.176V)
    private static final long ENGINE_VOLTAGE_MV = 27000;

    @Transactional
    public void process(VehicleModel vehicle, VehiclePayloadMqttDTO p) {

        ZonedDateTime eventTime = parseEventTime(p);
        FuelEfficiencyStatus newStatus = determinateStatus(p);

        VehicleStateCurrentModel current = vehicleStateCurrentRepository
                .findByVehicleModel_Id(vehicle.getId())
                .orElse(null);

        // Primer evento: solo inicializa estado (no hay delta previo)
        if (current == null) {
            VehicleStateCurrentModel init = new VehicleStateCurrentModel();
            init.setVehicleModel(vehicle);
            init.setStatus(newStatus);
            init.setLastEventTime(eventTime);
            vehicleStateCurrentRepository.save(init);
            return;
        }

        ZonedDateTime lastTime = current.getLastEventTime();

        // Si llega repetido EXACTO, no hay delta real
        if (eventTime.isEqual(lastTime)) {
            return;
        }

        // Si llega desordenado (menor), ignora
        if (eventTime.isBefore(lastTime)) {
            return;
        }

        long deltaSeconds = Duration.between(lastTime, eventTime).getSeconds();
        if (deltaSeconds <= 0) {
            return;
        }

        // Acumula el delta en el estado ANTERIOR (lo que estuvo vigente entre lastTime y eventTime)
        FuelEfficiencyStatus prevStatus = current.getStatus();
        accumulateSplitByDay(vehicle.getId(), lastTime, eventTime, prevStatus);

        // Actualiza estado actual
        current.setStatus(newStatus);
        current.setLastEventTime(eventTime);
        vehicleStateCurrentRepository.save(current);
    }

    private ZonedDateTime parseEventTime(VehiclePayloadMqttDTO p) {
        if (p == null || p.getTimestamp() == null) {
            // fallback: si no viene timestamp, usa ahora (no ideal, pero evita NPE)
            return ZonedDateTime.now(ZONE);
        }

        long ts = Long.parseLong(String.valueOf(p.getTimestamp()));

        // Si parece timestamp en segundos (10 dígitos aprox), conviértelo a ms
        if (ts < 1_000_000_000_000L) { // < 10^12
            ts = ts * 1000L;
        }

        return Instant.ofEpochMilli(ts).atZone(ZONE);
    }

    /**
     * Reglas:
     * - ESTACIONADO: contacto/ignición OFF
     * - RALENTI: ignición ON pero sin movimiento (y/o motor no operando según voltaje)
     * - OPERACION: ignición ON + movimiento + voltaje alto (o voltaje no disponible)
     */
    private FuelEfficiencyStatus determinateStatus(VehiclePayloadMqttDTO p) {
        Boolean ignition = p.getIgnitionInfo(); // true = contacto/ignición activa
        if (ignition == null || !ignition) {
            return FuelEfficiencyStatus.ESTACIONADO;
        }

        boolean moving = isMoving(p);
        Long extV = getExternalVoltageMv(p);

        boolean voltageHigh = (extV == null) || (extV >= ENGINE_VOLTAGE_MV);

        // Operación: movimiento + voltaje alto (o voltaje no disponible)
        if (moving && voltageHigh) {
            return FuelEfficiencyStatus.OPERACION;
        }

        // Con ignición activa pero sin operar -> Ralenti
        return FuelEfficiencyStatus.RALENTI;
    }

    private boolean isMoving(VehiclePayloadMqttDTO p) {
        // Prioridad: movement/instantMovement si llegan; si no, velocidad
        Integer movement = p.getMovement();           // 0/1
        Integer instant = p.getInstantMovement();     // 0/1

        if (movement != null && movement == 1) return true;
        if (instant != null && instant == 1) return true;

        // Velocidad GPS
        Double speed = p.getSpeed(); // km/h
        if (speed != null && speed > 0.0) return true;

        // Velocidad por IO 37
        Integer vehicleSpeedIo = p.getVehicleSpeedIo();
        return vehicleSpeedIo != null && vehicleSpeedIo > 0;
    }

    private Long getExternalVoltageMv(VehiclePayloadMqttDTO p) {
        if (p.getExternalVoltage() == null) return null;
        return Long.valueOf(p.getExternalVoltage());
    }

    /**
     * Divide el rango from->to por día (LocalDate) y acumula segundos.
     * Evita que un tramo caiga todo en un solo día cuando cruza medianoche.
     */
    private void accumulateSplitByDay(Long vehicleId, ZonedDateTime from, ZonedDateTime to, FuelEfficiencyStatus status) {
        ZonedDateTime cursor = from;

        while (cursor.toLocalDate().isBefore(to.toLocalDate())) {
            ZonedDateTime endOfDay = cursor.toLocalDate()
                    .plusDays(1)
                    .atStartOfDay(ZONE);

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
