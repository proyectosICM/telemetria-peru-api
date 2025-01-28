package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.models.GasRecordModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.GasRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GasRecordHandler {
    private final GasRecordRepository gasRecordRepository;

    public void saveGasRecordModel(VehiclePayloadMqttDTO data, VehicleModel vehicleModel){
        GasRecordModel gasRecordModel = new GasRecordModel();
        gasRecordModel.setIsVehicleOn(data.getIgnitionInfo());
        gasRecordModel.setLastPressureDetected(data.getGasInfo());
        gasRecordModel.setVehicleModel(vehicleModel);

        gasRecordRepository.save(gasRecordModel);
    }
}
