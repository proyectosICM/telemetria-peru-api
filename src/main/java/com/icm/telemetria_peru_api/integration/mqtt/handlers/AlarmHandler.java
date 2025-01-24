package com.icm.telemetria_peru_api.integration.mqtt.handlers;

import com.icm.telemetria_peru_api.models.AlarmRecordModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.AlarmRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlarmHandler {
    private final AlarmRecordRepository alarmRecordRepository;

    /**
     * Handles alarm information by checking if an alarm event exists
     * and saving a record associated with the vehicle if valid.
     *
     * @param vehicleModel the vehicle model associated with the alarm
     * @param alarmInfo    the alarm information value; a non-null and non-zero value indicates an active alarm
     */
    public void saveAlarmRecord(VehicleModel vehicleModel, Integer alarmInfo) {
        if (alarmInfo == null || alarmInfo == 0) {
            return;
        }
        AlarmRecordModel alarmRecordModel = new AlarmRecordModel();
        alarmRecordModel.setVehicleModel(vehicleModel);
        alarmRecordRepository.save(alarmRecordModel);
    }
}
