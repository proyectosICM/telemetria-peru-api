package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.GasRecordModel;
import com.icm.telemetria_peru_api.models.ImpactIncidentLoggingModel;
import com.icm.telemetria_peru_api.repositories.ImpactIncidentLoggingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImpactIncidentLoggingService {
    @Autowired
    private ImpactIncidentLoggingRepository impactIncidentLoggingRepository;

    public List<ImpactIncidentLoggingModel> findAll(){
        return impactIncidentLoggingRepository.findAll();
    }

    public Page<ImpactIncidentLoggingModel> findAll(Pageable pageable){
        return impactIncidentLoggingRepository.findAll(pageable);
    }

    public ImpactIncidentLoggingModel findById(Long id){
        return impactIncidentLoggingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
    }

    public List<ImpactIncidentLoggingModel> findByVehicleId(Long vehicleId){
        return impactIncidentLoggingRepository.findByVehicleModelId(vehicleId);
    }

    public Page<ImpactIncidentLoggingModel> findByVehicleId(Long vehicleId, Pageable pageable){
        return impactIncidentLoggingRepository.findByVehicleModelId(vehicleId, pageable);
    }

    /** More CRUD methods **/
    public ImpactIncidentLoggingModel save(ImpactIncidentLoggingModel impactIncidentLoggingModel){
        return impactIncidentLoggingRepository.save(impactIncidentLoggingModel);
    }

    public void deleteById(Long id){
        impactIncidentLoggingRepository.deleteById(id);
    }
}
