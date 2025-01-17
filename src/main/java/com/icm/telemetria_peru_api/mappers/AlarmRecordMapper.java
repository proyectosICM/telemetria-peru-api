package com.icm.telemetria_peru_api.mappers;

import com.icm.telemetria_peru_api.dto.AlarmRecordDTO;
import com.icm.telemetria_peru_api.models.AlarmRecordModel;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class AlarmRecordMapper {
    public AlarmRecordDTO mapToDTO(AlarmRecordModel alarmRecordModel){
        Long vehicleId = alarmRecordModel.getVehicleModel().getId();
        String licensePlate = alarmRecordModel.getVehicleModel().getLicensePlate();

        ZonedDateTime createdAt = alarmRecordModel.getCreatedAt();

        return new AlarmRecordDTO(alarmRecordModel.getId(),vehicleId, licensePlate, createdAt);
    }
}
