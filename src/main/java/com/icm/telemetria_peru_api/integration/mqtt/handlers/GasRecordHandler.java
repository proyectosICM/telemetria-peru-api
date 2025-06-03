package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.enums.FuelType;
import com.icm.telemetria_peru_api.models.GasRecordModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.GasRecordRepository;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class GasRecordHandler {
    private final GasRecordRepository gasRecordRepository;
    private final VehicleRepository vehicleRepository;

    public void saveGasRecordModel(VehiclePayloadMqttDTO data, VehicleModel vehicleModel) {
        GasRecordModel lastRecord = gasRecordRepository.findTopByVehicleModelIdOrderByCreatedAtDesc(vehicleModel.getId());

        VehicleModel dataVehicle = vehicleRepository.findByImei(data.getImei()).orElse(null);
        if (!dataVehicle.getFuelType().equals(FuelType.GAS)) {
            return;
        }

        // Formatear la nueva presión
        double newPressure = BigDecimal.valueOf(data.getFuelInfo() / 100)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        // Si no hay registro previo, crea uno nuevo
        if (lastRecord == null) {
            createNewGasRecord(vehicleModel, data, newPressure);
            return;
        }

        // Si la presión es distinta y el vehículo está encendido, crear un nuevo registro
        if (!lastRecord.getLastPressureDetected().equals(newPressure) && data.getIgnitionInfo()) {
            closeGasRecord(lastRecord, data);
            createNewGasRecord(vehicleModel, data, newPressure);
        } else {
            accumulateGasRecord(lastRecord, data);
        }
    }


    public void createNewGasRecord(VehicleModel vehicleModel, VehiclePayloadMqttDTO data, double formattedPressure){
        GasRecordModel gasRecordModel = new GasRecordModel();
        Long timestampInt = Long.parseLong(data.getTimestamp());
        gasRecordModel.setVehicleModel(vehicleModel);
        gasRecordModel.setStartTime(timestampInt);
        gasRecordModel.setLastPressureDetected(formattedPressure);
        gasRecordModel.setAccumulatedTime(0L);

        gasRecordRepository.save(gasRecordModel);
    }

    public void accumulateGasRecord(GasRecordModel lastRecord, VehiclePayloadMqttDTO data) {
        // Obtener el timestamp actual y convertirlo a ZonedDateTime

        Long currentTimestampInt = Long.parseLong(data.getTimestamp());
        Long newAccumulatedTime = currentTimestampInt - lastRecord.getStartTime() + lastRecord.getAccumulatedTime();
        // Actualizar el registro con el nuevo tiempo acumulado
        lastRecord.setAccumulatedTime(newAccumulatedTime);
        lastRecord.setStartTime(currentTimestampInt); // Actualiza el timestamp de inicio

        // Guardar el registro actualizado
        gasRecordRepository.save(lastRecord);
    }

    public void closeGasRecord(GasRecordModel lastRecord, VehiclePayloadMqttDTO data){
        // Obtener timestamp actual en zona horaria de Perú
        LocalDateTime nowInPeru = LocalDateTime.now(ZoneId.of("America/Lima"));

        // Calcular la diferencia entre ahora y el createdAt del registro
        Duration duration = Duration.between(lastRecord.getCreatedAt(), nowInPeru);
        long secondsElapsed = duration.getSeconds();

        // Establecer fin y tiempo acumulado
        lastRecord.setEndTime(Long.parseLong(data.getTimestamp()));
        lastRecord.setAccumulatedTime(secondsElapsed);

        gasRecordRepository.save(lastRecord);
    }
}
