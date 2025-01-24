package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.icm.telemetria_peru_api.models.GasChangeModel;
import com.icm.telemetria_peru_api.repositories.GasChangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor
public class GasChangeHandler {

    private final GasChangeRepository gasChangeRepository;

    public void saveGasChangeRecord(ZonedDateTime timestamp, Double pressure, Long vehicleId) {
        GasChangeModel lastRecord = gasChangeRepository.findTopByVehicleModelIdOrderByCreatedAtDesc(vehicleId);

        if (lastRecord == null) {
            createNewGasChangeRecord(vehicleId, timestamp, pressure);
            return;
        }

        if (lastRecord.getPressureBeforeChange() != pressure) {
            closeLastRecord(lastRecord, timestamp);
            createNewGasChangeRecord(vehicleId, timestamp, pressure);
        }
    }

    public void createNewGasChangeRecord(Long vehicleId, ZonedDateTime timestamp, Double pressure) {
        GasChangeModel gasChangeModel = new GasChangeModel();
        gasChangeModel.getVehicleModel().setId(vehicleId);
        gasChangeModel.setChangeDate(timestamp);
        gasChangeModel.setLowPressureDetectedAt(timestamp);
        gasChangeModel.setPressureBeforeChange(pressure);

        gasChangeRepository.save(gasChangeModel);
    }

    public void closeLastRecord(GasChangeModel lastRecord, ZonedDateTime timestamp) {
        lastRecord.setChangePerformedAt(timestamp);
        lastRecord.setPressureAfterChange(lastRecord.getPressureAfterChange());
        gasChangeRepository.save(lastRecord);
    }
}
