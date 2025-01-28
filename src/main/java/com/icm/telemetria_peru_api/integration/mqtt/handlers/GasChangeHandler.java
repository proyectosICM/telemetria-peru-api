package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.models.GasChangeModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.GasChangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GasChangeHandler {

    private final GasChangeRepository gasChangeRepository;

    public void saveGasChangeRecord(VehiclePayloadMqttDTO data, VehicleModel vehicleModel) {
        GasChangeModel lastRecord = gasChangeRepository.findTopByVehicleModelIdOrderByCreatedAtDesc(vehicleModel.getId());

        if (lastRecord == null) {
            createNewGasChangeRecord(vehicleModel.getId(), data.getTimestamp(), data.getGasInfo());
            return;
        }

        if (lastRecord.getPressureBeforeChange() != data.getGasInfo()) {
            closeLastRecord(lastRecord, data.getTimestamp());
            createNewGasChangeRecord(vehicleModel.getId(), data.getTimestamp(), data.getGasInfo());
        }
    }

    public void createNewGasChangeRecord(Long vehicleId, String timestamp, Double pressure) {
        GasChangeModel gasChangeModel = new GasChangeModel();
        gasChangeModel.getVehicleModel().setId(vehicleId);
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
