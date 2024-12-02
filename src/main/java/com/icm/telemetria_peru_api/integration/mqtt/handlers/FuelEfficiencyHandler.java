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

@Component
@RequiredArgsConstructor
public class FuelEfficiencyHandler {
    private final FuelEfficiencyRepository fuelEfficiencyRepository;

    public void processFuelEfficiencyInfo(VehicleModel vehicleModel, VehiclePayloadMqttDTO jsonNode) {
        FuelEfficiencyStatus determinate = determinateStatus(jsonNode.getIgnitionInfo(), jsonNode.getSpeed());

        FuelEfficiencyModel lastRecord = fuelEfficiencyRepository.findTopByVehicleModelIdOrderByCreatedAtDesc(vehicleModel.getId());

        if (lastRecord == null) {
            FuelEfficiencyModel newRecord = new FuelEfficiencyModel();
            newRecord.setFuelEfficiencyStatus(determinate);
            newRecord.setVehicleModel(vehicleModel);
            newRecord.setInitialFuel(jsonNode.getFuelInfo());
            fuelEfficiencyRepository.save(newRecord);
        }

        if (lastRecord != null && lastRecord.getFuelEfficiencyStatus() != determinate) {
            //Cierra el registro anterior
            lastRecord.setEndTime(ZonedDateTime.now());
            lastRecord.setFinalFuel(jsonNode.getFuelInfo());
            fuelEfficiencyRepository.save(lastRecord);

            //Crear e nuevo registro
            FuelEfficiencyModel newRecord = new FuelEfficiencyModel();
            newRecord.setFuelEfficiencyStatus(determinate);
            newRecord.setVehicleModel(vehicleModel);
            newRecord.setInitialFuel(jsonNode.getFuelInfo());
            fuelEfficiencyRepository.save(newRecord);
        }

        // Agregar un nuevo registro de velocidad
        if (lastRecord != null && lastRecord.getFuelEfficiencyStatus() == determinate) {
            if (jsonNode.getSpeed() != null && jsonNode.getSpeed() >= 1.0) {
                if (lastRecord.getSpeeds() == null) {
                    lastRecord.setSpeeds(new ArrayList<>());
                }
                lastRecord.getSpeeds().add(jsonNode.getSpeed());
                fuelEfficiencyRepository.save(lastRecord);
            }
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
}
