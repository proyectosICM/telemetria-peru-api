package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.enums.FuelType;
import com.icm.telemetria_peru_api.models.GasChangeModel;
import com.icm.telemetria_peru_api.models.GasRecordModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.GasRecordRepository;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor
public class GasRecordHandler {
    private final GasRecordRepository gasRecordRepository;
    private final VehicleRepository vehicleRepository;

    public void saveGasRecordModel(VehiclePayloadMqttDTO data, VehicleModel vehicleModel) {
        // Buscar el último registro de gas para este vehículo
        GasRecordModel lastRecord = gasRecordRepository.findTopByVehicleModelIdOrderByCreatedAtDesc(vehicleModel.getId());


        VehicleModel dataVehicle = vehicleRepository.findByImei(data.getImei()).orElse(null);

        if (!dataVehicle.getFuelType().equals(FuelType.GAS)) {
            return;
        }

        // Si no hay un registro previo, crea uno nuevo
        if (lastRecord == null) {
            createNewGasRecord(vehicleModel, data);
            return;
        }

        // Si la presión cambia, crea un nuevo registro
        if (!lastRecord.getLastPressureDetected().equals(data.getGasInfo())) {
            closeGasRecord(lastRecord, data);
            createNewGasRecord(vehicleModel, data);
        } else {
            // Si la presión no cambia, acumula el tiempo
            accumulateGasRecord(lastRecord, data);
        }
    }

    public void createNewGasRecord(VehicleModel vehicleModel, VehiclePayloadMqttDTO data){
        GasRecordModel gasRecordModel = new GasRecordModel();
        Long timestampInt = Long.parseLong(data.getTimestamp());
        gasRecordModel.setVehicleModel(vehicleModel);
        gasRecordModel.setStartTime(timestampInt);
        gasRecordModel.setLastPressureDetected(data.getGasInfo());
        gasRecordModel.setAccumulatedTime(0L);

        gasRecordRepository.save(gasRecordModel);
    }

    public void accumulateGasRecord(GasRecordModel lastRecord, VehiclePayloadMqttDTO data) {
        // Obtener el timestamp actual y convertirlo a ZonedDateTime
        Long newAccumulatedTime = lastRecord.getEndTime() - lastRecord.getStartTime() + lastRecord.getAccumulatedTime();
        Long currentTimestampInt = Long.parseLong(data.getTimestamp());
        // Actualizar el registro con el nuevo tiempo acumulado
        lastRecord.setAccumulatedTime(newAccumulatedTime);
        lastRecord.setStartTime(currentTimestampInt); // Actualiza el timestamp de inicio

        // Guardar el registro actualizado
        gasRecordRepository.save(lastRecord);
    }

    public void closeGasRecord(GasRecordModel lastRecord, VehiclePayloadMqttDTO data){
        Long currentTimestampInt = Long.parseLong(data.getTimestamp());
        lastRecord.setEndTime(currentTimestampInt);

        gasRecordRepository.save(lastRecord);
    }
}
