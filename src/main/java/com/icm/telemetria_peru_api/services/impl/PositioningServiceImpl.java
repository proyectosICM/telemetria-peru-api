package com.icm.telemetria_peru_api.services.impl;

import com.icm.telemetria_peru_api.models.PositioningModel;
import com.icm.telemetria_peru_api.repositories.PositioningRepository;
import com.icm.telemetria_peru_api.services.PositioningService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositioningServiceImpl implements PositioningService {
    private final PositioningRepository positioningRepository;

    @Override
    public List<PositioningModel> findAll() {
        return positioningRepository.findAll();
    }

    @Override
    public PositioningModel findById(Long id) {
        return positioningRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
    }

    @Override
    public List<PositioningModel> findByVehicleTypeId(Long vehicleTypeId) {
        return positioningRepository.findByVehicleTypeModelId(vehicleTypeId);
    }

    @Override
    public PositioningModel save(PositioningModel positioning) {
        return positioningRepository.save(positioning);
    }

    @Override
    public void deleteById(Long id) {
        positioningRepository.deleteById(id);
    }
}
