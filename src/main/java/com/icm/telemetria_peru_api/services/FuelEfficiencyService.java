package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.FuelEfficiencyModel;
import com.icm.telemetria_peru_api.repositories.FuelEfficiencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuelEfficiencyService {
    @Autowired
    private FuelEfficiencyRepository fuelEfficiencyRepository;

    public List<FuelEfficiencyModel> findByVehicleModelId(Long vehicleId){
        return fuelEfficiencyRepository.findByVehicleModelId(vehicleId);
    }

    public Page<FuelEfficiencyModel> findByVehicleModelId(Long vehicleId, Pageable pageable){
        return fuelEfficiencyRepository.findByVehicleModelId(vehicleId, pageable);
    }

    public FuelEfficiencyModel save(FuelEfficiencyModel fuelEfficiencyModel){
        return fuelEfficiencyRepository.save(fuelEfficiencyModel);
    }
}
