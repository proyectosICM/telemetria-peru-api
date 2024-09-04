package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.BatteryModel;
import com.icm.telemetria_peru_api.models.GasRecordModel;
import com.icm.telemetria_peru_api.repositories.BatteryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatteryService {
    @Autowired
    private BatteryRepository batteryRepository;

    public List<BatteryModel> findAll(){
        return batteryRepository.findAll();
    }

    public Page<BatteryModel> findAll(Pageable pageable){
        return batteryRepository.findAll(pageable);
    }

    public BatteryModel findById(Long id){
        return batteryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
    }

    public List<BatteryModel> findByVehicleId(Long vehicleId){
        return batteryRepository.findByVehicleModelId(vehicleId);
    }

    public Page<BatteryModel> findByVehicleId(Long vehicleId, Pageable pageable){
        return batteryRepository.findByVehicleModelId(vehicleId, pageable);
    }

    /** More CRUD methods **/
    public BatteryModel save(BatteryModel batteryModel){
        return batteryRepository.save(batteryModel);
    }

    public BatteryModel update(BatteryModel batteryModel, Long id){
        BatteryModel existing = findById(id);
        existing.setName(batteryModel.getName());
        if(batteryModel.getVehicleModel() != null){
            existing.setVehicleModel(batteryModel.getVehicleModel());
        }
        return batteryRepository.save(existing);
    }
    public void deleteById(Long id){
        batteryRepository.deleteById(id);
    }
}
