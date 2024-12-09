package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.enums.FuelEfficiencyStatus;
import com.icm.telemetria_peru_api.models.FuelEfficiencyModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.FuelEfficiencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FuelEfficiencyHandler {
    private final FuelEfficiencyRepository fuelEfficiencyRepository;

    private void createNewFuelEfficiencyRecord(VehicleModel vehicleModel, VehiclePayloadMqttDTO jsonNode, FuelEfficiencyStatus status) {
        FuelEfficiencyModel newRecord = new FuelEfficiencyModel();
        newRecord.setFuelEfficiencyStatus(status);
        newRecord.setVehicleModel(vehicleModel);
        newRecord.setInitialFuel(jsonNode.getFuelInfo());
        fuelEfficiencyRepository.save(newRecord);
    }

    private void addNewSpeedToRecord(FuelEfficiencyModel lastRecord, VehiclePayloadMqttDTO jsonNode) {
        if (jsonNode.getSpeed() != null && jsonNode.getSpeed() >= 1.0) {
            if (lastRecord.getSpeeds() == null) {
                lastRecord.setSpeeds(new ArrayList<>());
            }
            lastRecord.getSpeeds().add(jsonNode.getSpeed());
            fuelEfficiencyRepository.save(lastRecord);
        }
    }

    private void closeLastRecord(FuelEfficiencyModel lastRecord, VehiclePayloadMqttDTO jsonNode) {
        lastRecord.setEndTime(ZonedDateTime.now());
        lastRecord.setFinalFuel(jsonNode.getFuelInfo());
        lastRecord.setCoordinates(jsonNode.getCoordinates());

        double accumulatedHours = calculateElapsedTimeInHours(lastRecord.getStartTime(), ZonedDateTime.now());
        lastRecord.setAccumulatedHours(accumulatedHours);
        calculateDistanceAndEfficiency(lastRecord, accumulatedHours, jsonNode.getFuelInfo());
        calculateEfficiencyByHour(lastRecord, accumulatedHours, jsonNode.getFuelInfo());
        fuelEfficiencyRepository.save(lastRecord);
    }

    public void processFuelEfficiencyInfo(VehicleModel vehicleModel, VehiclePayloadMqttDTO jsonNode) {
        FuelEfficiencyStatus determinate = determinateStatus(jsonNode.getIgnitionInfo(), jsonNode.getSpeed());

        FuelEfficiencyModel lastRecord = fuelEfficiencyRepository.findTopByVehicleModelIdOrderByCreatedAtDesc(vehicleModel.getId());

        if (lastRecord == null) {
            createNewFuelEfficiencyRecord(vehicleModel, jsonNode, determinate);
            return;
        }

        if (lastRecord.getFuelEfficiencyStatus() != determinate) {
            closeLastRecord(lastRecord, jsonNode);
            createNewFuelEfficiencyRecord(vehicleModel, jsonNode, determinate);
        } else {
            addNewSpeedToRecord(lastRecord, jsonNode);
        }
    }

    private FuelEfficiencyStatus determinateStatus(Boolean ignitionInfo, Double speed) {
        if (ignitionInfo == null && speed == 0) {
            return FuelEfficiencyStatus.ESTACIONADO;
        }

        if (ignitionInfo != null && ignitionInfo) {
            if (speed == 0) {
                return FuelEfficiencyStatus.RALENTI;
            } else if (speed > 0) {
                return FuelEfficiencyStatus.OPERACION;
            }
        }
        return FuelEfficiencyStatus.ESTACIONADO;
    }

    private double calculateElapsedTimeInHours(ZonedDateTime startTime, ZonedDateTime endTime) {
        if (startTime != null && endTime != null) {
            long elapsedMillis = java.time.Duration.between(startTime, endTime).toMillis();
            return elapsedMillis / 3600000.0;  // Convertir milisegundos a horas
        }
        return 0.0;
    }

    private void calculateDistanceAndEfficiency(FuelEfficiencyModel record, double hours, double finalFuel) {
        List<Double> speeds = record.getSpeeds();
        if (speeds != null && !speeds.isEmpty()) {
            double totalSpeed = speeds.stream().mapToDouble(Double::doubleValue).sum();
            double averageSpeed = totalSpeed / speeds.size();
            double distance = averageSpeed * hours;
            record.setDistance(distance);

            double fuelUsed = record.getInitialFuel() - finalFuel;
            if (fuelUsed > 0) {
                double fuelEfficiency = distance / fuelUsed;
                record.setFuelEfficiency(fuelEfficiency);
            }
        }
    }

    private void calculateEfficiencyByHour(FuelEfficiencyModel record, double hours, double finalFuel) {
        if (hours > 0) {
            double fuelUsed = record.getInitialFuel() - finalFuel;
            if (fuelUsed > 0) {
                double efficiencyByHour = fuelUsed / hours;
                record.setFuelConsumptionPerHour(efficiencyByHour);
            } else {
                record.setFuelConsumptionPerHour(0.0);
            }
        } else {
            record.setFuelConsumptionPerHour(0.0);
        }
    }
}
