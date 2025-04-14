package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.models.VehicleFuelReportModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.VehicleFuelReportRepositpory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FuelReportHandler {
    private final VehicleFuelReportRepositpory vehicleFuelReportRepositpory;

    public void saveFuelReport(VehiclePayloadMqttDTO data, VehicleModel vehicleModel) {
        try {
            long epochSeconds = Long.parseLong(data.getTimestamp());
            LocalDateTime now = LocalDateTime.ofEpochSecond(epochSeconds, 0, java.time.ZoneOffset.UTC);
            Optional<VehicleFuelReportModel> optionalLast = vehicleFuelReportRepositpory
                    .findTopByVehicleModelIdOrderByCreatedAtDesc(vehicleModel.getId());

            // Si no hay ningún registro, crear uno nuevo
            if (optionalLast.isEmpty()) {
                VehicleFuelReportModel newReport = new VehicleFuelReportModel();
                newReport.setVehicleModel(vehicleModel);
                newReport.setDate(now.toLocalDate());
                newReport.setOpeningTime(now);
                newReport.setInitialFuel(data.getFuelInfo());
                newReport.setFinalFuel(data.getFuelInfo());
                newReport.setIdleTime(Duration.ZERO);
                newReport.setParkedTime(Duration.ZERO);
                newReport.setOperatingTime(Duration.ZERO);

                vehicleFuelReportRepositpory.save(newReport);
                return;
            }

            VehicleFuelReportModel report = optionalLast.get();

            // Verificar si ya está cerrado
            if (report.getClosingTime() != null) {
                createNewReport(vehicleModel, now, data.getFuelInfo());
                return;
            }

            // Calcular tiempo transcurrido
            LocalDateTime lastUpdate = report.getUpdatedAt().toLocalDateTime();
            Duration elapsed = Duration.between(lastUpdate, now);
            if (elapsed.isNegative() || elapsed.isZero()) return;

            // Calcular lógica de estados
            boolean ignitionOn = Boolean.TRUE.equals(data.getIgnitionInfo());
            double speed = data.getSpeed() != null ? data.getSpeed() : 0.0;

            if (!ignitionOn) {
                report.setParkedTime(report.getParkedTime().plus(elapsed));
            } else if (speed < 5) {
                report.setIdleTime(report.getIdleTime().plus(elapsed));
            } else {
                report.setOperatingTime(report.getOperatingTime().plus(elapsed));
            }

            // ⚠️ Detectar cierre por cambio de día
            boolean isNewDay = !report.getOpeningTime().toLocalDate().equals(now.toLocalDate());

            // ⚠️ Detectar carga de combustible
            Double previousFuel = report.getFinalFuel() != null ? report.getFinalFuel() : report.getInitialFuel();
            Double currentFuel = data.getFuelInfo();
            boolean fuelIncreaseDetected = (currentFuel - previousFuel) > 15;

            if (isNewDay || fuelIncreaseDetected) {
                // Cerrar reporte actual (NO tomar nueva lectura como final)
                report.setClosingTime(now);
                report.setFinalFuel(previousFuel); // se cierra con el combustible previo
                vehicleFuelReportRepositpory.save(report);

                // Crear nuevo con el nuevo fuelInfo (si fue carga)
                createNewReport(vehicleModel, now, currentFuel);
                return;
            }

            // Caso normal: solo actualizar con nuevo combustible
            report.setFinalFuel(currentFuel);
            vehicleFuelReportRepositpory.save(report);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error Saving Fuel Report");
        }
    }

    private void createNewReport(VehicleModel vehicleModel, LocalDateTime now, Double fuelLevel) {
        VehicleFuelReportModel newReport = new VehicleFuelReportModel();
        newReport.setVehicleModel(vehicleModel);
        newReport.setDate(now.toLocalDate());
        newReport.setOpeningTime(now);
        newReport.setInitialFuel(fuelLevel);
        newReport.setFinalFuel(fuelLevel); // iniciamos igual
        newReport.setIdleTime(Duration.ZERO);
        newReport.setParkedTime(Duration.ZERO);
        newReport.setOperatingTime(Duration.ZERO);
        vehicleFuelReportRepositpory.save(newReport);
    }
}
