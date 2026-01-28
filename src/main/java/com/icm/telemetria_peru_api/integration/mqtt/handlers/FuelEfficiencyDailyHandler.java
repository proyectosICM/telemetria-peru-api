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

        // Si llega desordenado o repetido, ignora (evita acumulados negativos / duplicados)
        if (!eventTime.isAfter(lastTime)) {
            // Igual puedes actualizar status si quieres, pero yo recomiendo ignorar.
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
        // Asumo timestamp en ms como String/Long
        long ts = Long.parseLong(String.valueOf(p.getTimestamp()));
        return Instant.ofEpochMilli(ts).atZone(ZONE);
    }

    /**
     * Reglas según lo que pediste:
     * - ESTACIONADO: motor apagado + contacto cerrado => ignition=false
     * - RALENTI: contacto abierto pero sin movimiento ni alza de voltaje (motor no operando)
     * - OPERACION: contacto abierto + motor operando (voltaje alto) + movimiento
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
        Integer movement = p.getMovement();           // 0/1 (si existe)
        Integer instant = p.getInstantMovement();     // 0/1 (si existe)

        if (movement != null && movement == 1) return true;
        if (instant != null && instant == 1) return true;

        // Velocidad GPS
        Double speed = p.getSpeed(); // km/h (si existe)
        if (speed != null && speed > 0.0) return true;

        // Velocidad por IO 37 (si existe)
        Integer vehicleSpeedIo = p.getVehicleSpeedIo();
        return vehicleSpeedIo != null && vehicleSpeedIo > 0;
    }

    private Long getExternalVoltageMv(VehiclePayloadMqttDTO p) {
        // Ajusta tipo según tu DTO (Long/Integer)
        if (p.getExternalVoltage() == null) return null;
        return Long.valueOf(p.getExternalVoltage());
    }

    /**
     * Divide el rango lastTime->eventTime por día (LocalDate) y acumula segundos.
     * Esto evita que un tramo caiga todo en un solo día cuando cruza medianoche.
     */
    private void accumulateSplitByDay(Long vehicleId, ZonedDateTime from, ZonedDateTime to, FuelEfficiencyStatus status) {

        ZonedDateTime cursor = from;

        while (cursor.toLocalDate().isBefore(to.toLocalDate())) {
            // fin del día actual: 23:59:59.999..., lo tomamos como start del siguiente día
            ZonedDateTime endOfDay = cursor.toLocalDate()
                    .plusDays(1)
                    .atStartOfDay(ZONE);

            long seconds = Duration.between(cursor, endOfDay).getSeconds();
            addToDaily(vehicleId, cursor.toLocalDate(), status, seconds);

            cursor = endOfDay;
        }

        // Último tramo (mismo día)
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
