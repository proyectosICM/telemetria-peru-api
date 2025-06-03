package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.AlarmRecordDTO;
import com.icm.telemetria_peru_api.mappers.AlarmRecordMapper;
import com.icm.telemetria_peru_api.models.AlarmRecordModel;
import com.icm.telemetria_peru_api.repositories.AlarmRecordRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmRecordService {
    private final AlarmRecordRepository alarmRecordRepository;
    private final AlarmRecordMapper alarmRecordMapper;

    @Transactional(readOnly = true)
    public List<AlarmRecordDTO> findByVehicleModelId(Long vehicleId){
        // Map AlarmRecordModel entities to DTOs for client-side consumption
        List<AlarmRecordModel> data = alarmRecordRepository.findByVehicleModelId(vehicleId);
        return data.stream()
                .map(alarmRecordMapper::mapToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<AlarmRecordDTO> findByVehicleModelId(Long vehicleId, Pageable pageable){
        // Return a paginated list of AlarmRecordDTOs mapped from entities
        Page<AlarmRecordModel> alarmRecordModels = alarmRecordRepository.findByVehicleModelId(vehicleId, pageable);
        List<AlarmRecordDTO> alarmRecordDTOS = alarmRecordModels.stream()
                .map(alarmRecordMapper::mapToDTO)
                .toList();
        return new PageImpl<>(alarmRecordDTOS, pageable, alarmRecordModels.getTotalElements());

    }

    public AlarmRecordModel save(AlarmRecordModel alarmRecordModel){
        return alarmRecordRepository.save(alarmRecordModel);
    }

    public void deleteById(Long id){
        alarmRecordRepository.deleteById(id);
    }

}
