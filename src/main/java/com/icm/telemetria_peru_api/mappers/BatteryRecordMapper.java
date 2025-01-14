package com.icm.telemetria_peru_api.mappers;

import com.icm.telemetria_peru_api.dto.BatteryRecordDTO;
import com.icm.telemetria_peru_api.models.BatteryRecordModel;
import org.springframework.stereotype.Component;

@Component
public class BatteryRecordMapper {
    /** Maps the BatteryRecord data to BatteryRecordDTO. **/
    public BatteryRecordDTO mapToDTO(BatteryRecordModel batteryRecordModel) {
        Long batteryId = batteryRecordModel.getBatteryModel().getId();
        String nameBattery = batteryRecordModel.getBatteryModel().getName();

        return new BatteryRecordDTO(
                batteryRecordModel.getId(),
                batteryRecordModel.getVoltage(),
                batteryId,
                nameBattery,
                batteryRecordModel.getCreatedAt()
        );
    }
}