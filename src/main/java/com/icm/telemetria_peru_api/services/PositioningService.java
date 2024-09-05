package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.PositioningModel;
import com.icm.telemetria_peru_api.repositories.PositioningRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PositioningService {
    @Autowired
    private PositioningRepository positioningRepository;

    public List<PositioningModel> findAll() {
        return positioningRepository.findAll();
    }

    public PositioningModel findById(Long id) {
        return positioningRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
    }

    public List<PositioningModel> findByVehicleTypeId(Long vehicleTypeId) {
        return positioningRepository.findByVehicleTypeModelId(vehicleTypeId);
    }

    public PositioningModel save(PositioningModel positioning) {
        return positioningRepository.save(positioning);
    }

    public void deleteById(Long id) {
        positioningRepository.deleteById(id);
    }
}
