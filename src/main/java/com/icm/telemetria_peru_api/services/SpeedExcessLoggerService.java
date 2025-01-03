package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.SpeedExcessLoggerModel;
import com.icm.telemetria_peru_api.repositories.SpeedExcessLoggerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpeedExcessLoggerService {
    private final SpeedExcessLoggerRepository speedExcessLoggerRepository;

    public List<SpeedExcessLoggerModel> findAll(){
        return speedExcessLoggerRepository.findAll();
    }

    public Page<SpeedExcessLoggerModel> findAll(Pageable pageable){
        return speedExcessLoggerRepository.findAll(pageable);
    }

    public SpeedExcessLoggerModel findById(Long id){
        return speedExcessLoggerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
    }

    public List<SpeedExcessLoggerModel> findByVehicleId(Long vehicleId){
        return speedExcessLoggerRepository.findByVehicleModelId(vehicleId);
    }

    public Page<SpeedExcessLoggerModel> findByVehicleId(Long vehicleId, Pageable pageable){
        return speedExcessLoggerRepository.findByVehicleModelId(vehicleId, pageable);
    }

    /** More CRUD methods **/
    public SpeedExcessLoggerModel save(SpeedExcessLoggerModel speedExcessLoggerModel){
        return speedExcessLoggerRepository.save(speedExcessLoggerModel);
    }

    public void deleteById(Long id){
        speedExcessLoggerRepository.deleteById(id);
    }
}
