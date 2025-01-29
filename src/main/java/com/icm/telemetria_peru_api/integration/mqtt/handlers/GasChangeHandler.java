package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.enums.FuelType;
import com.icm.telemetria_peru_api.models.GasChangeModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.GasChangeRepository;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GasChangeHandler {

    private final GasChangeRepository gasChangeRepository;
    private final VehicleRepository vehicleRepository;

    public void saveGasChangeRecord(VehiclePayloadMqttDTO data, VehicleModel vehicleModel) {
        GasChangeModel lastRecord = gasChangeRepository.findTopByVehicleModelIdOrderByCreatedAtDesc(vehicleModel.getId());

        VehicleModel dataVehicle = vehicleRepository.findByImei(data.getImei()).orElse(null);

        if (!dataVehicle.getFuelType().equals(FuelType.GAS)) {
            return;
        }

        if (lastRecord == null) {
            createNewGasChangeRecord(vehicleModel, data.getTimestamp(), data.getGasInfo());
            return;
        }

        if (lastRecord.getPressureBeforeChange() != data.getGasInfo()) {
            closeLastRecord(lastRecord, data.getTimestamp());
            createNewGasChangeRecord(vehicleModel, data.getTimestamp(), data.getGasInfo());
        }
    }

    public void createNewGasChangeRecord(VehicleModel vehicleModel, String timestamp, Double pressure) {
        GasChangeModel gasChangeModel = new GasChangeModel();
        gasChangeModel.setVehicleModel(vehicleModel);
        gasChangeModel.setLowPressureDetectedAt(timestamp);
        gasChangeModel.setPressureBeforeChange(pressure);

        gasChangeRepository.save(gasChangeModel);
    }

    public void closeLastRecord(GasChangeModel lastRecord, String timestamp) {
        lastRecord.setChangePerformedAt(timestamp);
        lastRecord.setPressureAfterChange(lastRecord.getPressureAfterChange());
        gasChangeRepository.save(lastRecord);
    }
}
