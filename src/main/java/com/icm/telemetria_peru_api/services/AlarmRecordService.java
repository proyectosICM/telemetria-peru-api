package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.AlarmRecordDTO;
import com.icm.telemetria_peru_api.models.AlarmRecordModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AlarmRecordService {
    List<AlarmRecordDTO> findByVehicleModelId(Long vehicleId);
    Page<AlarmRecordDTO> findByVehicleModelId(Long vehicleId, Pageable pageable);
    AlarmRecordModel save(AlarmRecordModel alarmRecordModel);
    void deleteById(Long id);
}
