package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.BatteryModel;
import com.icm.telemetria_peru_api.models.BatteryRecordModel;
import com.icm.telemetria_peru_api.repositories.BatteryRecordRepository;
import com.icm.telemetria_peru_api.repositories.BatteryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatteryRecordService {
    @Autowired
    private BatteryRecordRepository batteryRecordRepository;

    public List<BatteryRecordModel> findAll(){
        return batteryRecordRepository.findAll();
    }

    public Page<BatteryRecordModel> findAll(Pageable pageable){
        return batteryRecordRepository.findAll(pageable);
    }

    public BatteryRecordModel findById(Long id){
        return batteryRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
    }

    public List<BatteryRecordModel> findByBatteryId(Long batteryId){
        return batteryRecordRepository.findByBatteryModelId(batteryId);
    }

    public Page<BatteryRecordModel> findByBatteryId(Long batteryId, Pageable pageable){
        return batteryRecordRepository.findByBatteryModelId(batteryId, pageable);
    }

    /** More CRUD methods **/
    public BatteryRecordModel save(BatteryRecordModel batteryRecordModel){
        return batteryRecordRepository.save(batteryRecordModel);
    }

}
