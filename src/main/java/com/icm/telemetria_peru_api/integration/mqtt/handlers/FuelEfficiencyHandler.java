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

    public void processFuelEfficiencyInfo(VehicleModel vehicleModel, VehiclePayloadMqttDTO jsonNode) {
        FuelEfficiencyStatus determinate = determinateStatus(jsonNode.getIgnitionInfo(), jsonNode.getSpeed());

        FuelEfficiencyModel lastRecord = fuelEfficiencyRepository.findTopByVehicleModelIdOrderByCreatedAtDesc(vehicleModel.getId());

        if (lastRecord == null) {
            createNewFuelEfficiencyRecord(vehicleModel, jsonNode, determinate);
            return;
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
}
