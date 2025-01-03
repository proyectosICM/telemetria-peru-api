package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.AlarmRecordModel;
import com.icm.telemetria_peru_api.repositories.AlarmRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlarmRecordService {
    private final AlarmRecordRepository alarmRecordRepository;

    public List<AlarmRecordModel> findAll(){
        return alarmRecordRepository.findAll();
    }

    public Page<AlarmRecordModel> findAll(Pageable pageable){
        return alarmRecordRepository.findAll(pageable);
    }

    public List<AlarmRecordModel> findByVehicleModelId(Long vehicleId){
        return alarmRecordRepository.findByVehicleModelId(vehicleId);
    }

    public Page<AlarmRecordModel> findByVehicleModelId(Long vehicleId, Pageable pageable){
        return alarmRecordRepository.findByVehicleModelId(vehicleId, pageable);
    }

    public AlarmRecordModel save(AlarmRecordModel alarmRecordModel){
        return alarmRecordRepository.save(alarmRecordModel);
    }

    /* Stats */
    public List<Map<String, Object>> getHourlyAveragesByDate(LocalDate date) {
        return alarmRecordRepository.findHourlyAverageByDate(date);
    }

    public void deleteById(Long id){
        alarmRecordRepository.deleteById(id);
    }

}
