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

            if (optionalLast.isEmpty()) {
                VehicleFuelReportModel newReport = FuelReportFactory.create(vehicleModel, now, data.getFuelInfo());
                vehicleFuelReportRepositpory.save(newReport);
                return;
            }

            VehicleFuelReportModel report = optionalLast.get();

            if (report.getClosingTime() != null) {
                vehicleFuelReportRepositpory.save(FuelReportFactory.create(vehicleModel, now, data.getFuelInfo()));
                return;
            }

            // ⏱️ Actualizar tiempos de operación
            FuelReportCalculator.updateOperationTimes(report, data, now);

            boolean isNewDay = FuelReportConditions.isNewDay(report, now);
            boolean fuelIncreased = FuelReportConditions.fuelIncreased(report, data.getFuelInfo());

            if (isNewDay || fuelIncreased) {
                Double previousFuel = report.getFinalFuel() != null ? report.getFinalFuel() : report.getInitialFuel();
                report.setClosingTime(now);
                report.setFinalFuel(previousFuel);
                vehicleFuelReportRepositpory.save(report);

                vehicleFuelReportRepositpory.save(FuelReportFactory.create(vehicleModel, now, data.getFuelInfo()));
                return;
            }

            // Actualización normal
            report.setFinalFuel(data.getFuelInfo());
            vehicleFuelReportRepositpory.save(report);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error Saving Fuel Report");
        }
    }

    // 🔧 Utilidad para crear reportes nuevos
    private static class FuelReportFactory {
        public static VehicleFuelReportModel create(VehicleModel vehicleModel, LocalDateTime now, Double fuelLevel) {
            VehicleFuelReportModel report = new VehicleFuelReportModel();
            report.setVehicleModel(vehicleModel);
            report.setDate(now.toLocalDate());
            report.setOpeningTime(now);
            report.setInitialFuel(fuelLevel);
            report.setFinalFuel(fuelLevel);
            report.setIdleTime(Duration.ZERO);
            report.setParkedTime(Duration.ZERO);
            report.setOperatingTime(Duration.ZERO);
            return report;
        }
    }

    // 🔍 Lógica de condiciones
    private static class FuelReportConditions {
        public static boolean isNewDay(VehicleFuelReportModel report, LocalDateTime now) {
            return !report.getOpeningTime().toLocalDate().equals(now.toLocalDate());
        }

        public static boolean fuelIncreased(VehicleFuelReportModel report, Double newFuelLevel) {
            Double lastFuel = report.getFinalFuel() != null ? report.getFinalFuel() : report.getInitialFuel();
            return (newFuelLevel - lastFuel) > 15;
        }
    }

    // ⏱️ Lógica de actualización de tiempos
    private static class FuelReportCalculator {
        public static void updateOperationTimes(VehicleFuelReportModel report, VehiclePayloadMqttDTO data, LocalDateTime now) {
            LocalDateTime lastUpdate = report.getUpdatedAt().toLocalDateTime();
            Duration elapsed = Duration.between(lastUpdate, now);
            if (elapsed.isNegative() || elapsed.isZero()) return;

            boolean ignitionOn = Boolean.TRUE.equals(data.getIgnitionInfo());
            double speed = data.getSpeed() != null ? data.getSpeed() : 0.0;

            if (!ignitionOn) {
                report.setParkedTime(report.getParkedTime().plus(elapsed));
            } else if (speed < 5) {
                report.setIdleTime(report.getIdleTime().plus(elapsed));
            } else {
                report.setOperatingTime(report.getOperatingTime().plus(elapsed));
            }
        }
    }
}
