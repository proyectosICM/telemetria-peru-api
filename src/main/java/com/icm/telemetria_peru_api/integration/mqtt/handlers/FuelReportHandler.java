package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.models.VehicleFuelReportModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.VehicleFuelReportRepositpory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FuelReportHandler {
    private final VehicleFuelReportRepositpory vehicleFuelReportRepositpory;

        public void saveFuelReport(VehiclePayloadMqttDTO data, VehicleModel vehicleModel) {
        try {
            System.out.println("Data de : " + data.getImei());
            Optional<VehicleFuelReportModel> optionalLast = vehicleFuelReportRepositpory
                    .findTopByVehicleModelIdOrderByCreatedAtDesc(vehicleModel.getId());

            if (optionalLast.isEmpty()) {
                VehicleFuelReportModel newReport = createReport(data, vehicleModel);
                vehicleFuelReportRepositpory.save(newReport);
                return;
            }

            // Si hay un reporte previo, actualizamos tiempos y combustible
            VehicleFuelReportModel report = optionalLast.get();
            double currentFuel = report.getCurrentFuelDetected();
            double incomingFuel = data.getFuelInfo();

            // Obtener timestamp actual del mensaje
            long epochSeconds = Long.parseLong(data.getTimestamp());
            Instant instant = Instant.ofEpochSecond(epochSeconds);

            LocalDateTime now = Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.of("America/Lima")).toLocalDateTime();
            // System.out.println("Timestamp: " + data.getTimestamp());

            System.out.println("Ahora: " + now + " | Hora lógica: " + now.getHour());
            System.out.println("Creado: " + report.getCreatedAt().toLocalDateTime() + " | Hora lógica: " + report.getCreatedAt().toLocalDateTime().getHour());
            System.out.println("eepoch: " + epochSeconds);
            // Si el reporte fue creado hace más de 1 hora, cerramos el reportes
            // Si cambió de hora, cerrar el reporte actual

            if (now.getHour() != report.getCreatedAt().toLocalDateTime().getHour()) {
                System.out.println("⏰ Cambio de hora lógica, cerrando reporte: " + data.getImei());
                closeReport(data, report);
                vehicleFuelReportRepositpory.save(report);

                VehicleFuelReportModel newReport = createReport(data, vehicleModel);
                vehicleFuelReportRepositpory.save(newReport);
                return;
            }

            // ✅ Verificamos si hay una recarga significativa (>10) y no es un error de sensor (actual == 0)
            if (currentFuel > 0 && incomingFuel >= currentFuel + 20) {
                System.out.println("Recarga significativa: " + data.getImei());
                // 👉 Cerramos el reporte actual
                closeReport(data, report);
                vehicleFuelReportRepositpory.save(report);

                // 👉 Creamos uno nuevo con el nuevo valor
                VehicleFuelReportModel newReport = createReport(data, vehicleModel);
                vehicleFuelReportRepositpory.save(newReport);
                return;
            }

            // 👉 Acumulamos el tiempo del nuevo estado
            accumulateStatusTime(data, report);

            // 👉 Actualizamos el combustible actual
            report.setCurrentFuelDetected(data.getFuelInfo());

            // 👉 Finalmente, lo guardamos
            vehicleFuelReportRepositpory.save(report);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error Saving Fuel Report");
        }
    }

    public void accumulateStatusTime(VehiclePayloadMqttDTO data, VehicleFuelReportModel report) {
        try {
            System.out.println("Acumulando tiempo: " + data.getImei());
            // Obtener timestamp actual del mensaje
            long epochSeconds = Long.parseLong(data.getTimestamp());
            LocalDateTime now = LocalDateTime.ofEpochSecond(epochSeconds, 0, java.time.ZoneOffset.UTC);

            // Obtener la última hora de actualización del reporte
            LocalDateTime lastUpdate = report.getUpdatedAt().toLocalDateTime();

            // Calcular cuánto tiempo ha pasado desde la última actualización
            Duration elapsed = Duration.between(lastUpdate, now);
            if (elapsed.isNegative() || elapsed.isZero()) return;

            // Evaluar condiciones del vehículo
            boolean ignitionOn = Boolean.TRUE.equals(data.getIgnitionInfo());
            double speed = data.getSpeed() != null ? data.getSpeed() : 0.0;
            System.out.println("Estado del vehiculo: " + data.getImei() + " - Encendido: " + ignitionOn + " - Velocidad: " + speed);


            long seconds = elapsed.getSeconds();

            if (seconds <= 0) return;

            if (!ignitionOn) {
                report.setParkedSeconds(report.getParkedSeconds() + seconds);
            } else if (speed < 5) {
                report.setIdleSeconds(report.getIdleSeconds() + seconds);
            } else {
                report.setOperatingSeconds(report.getOperatingSeconds() + seconds);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Error acumulando tiempo de estado");
        }
    }

    public void closeReport(VehiclePayloadMqttDTO data, VehicleFuelReportModel report){
        report.setFinalFuel(data.getFuelInfo());
    }

    private VehicleFuelReportModel createReport(VehiclePayloadMqttDTO data, VehicleModel vehicleModel) {
        VehicleFuelReportModel newReport = new VehicleFuelReportModel();
        newReport.setVehicleModel(vehicleModel);
        newReport.setCurrentFuelDetected(data.getFuelInfo());
        newReport.setInitialFuel(data.getFuelInfo());
        newReport.setIdleSeconds(0L);
        newReport.setParkedSeconds(0L);
        newReport.setOperatingSeconds(0L);
        return newReport;
    }
}

