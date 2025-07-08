package com.icm.telemetria_peru_api.services.impl;

import com.icm.telemetria_peru_api.models.TirePositioningModel;
import com.icm.telemetria_peru_api.repositories.TirePositioningRepository;
import com.icm.telemetria_peru_api.services.TirePositioningService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TirePositioningServiceImpl implements TirePositioningService {

    private final TirePositioningRepository tirePositioningRepository;


    @Override
    public List<TirePositioningModel> findAll() {
        return tirePositioningRepository.findAll();
    }

    @Override
    public List<TirePositioningModel> findByVehicleModelId(Long vehicleId) {
        return tirePositioningRepository.findByVehicleModelId(vehicleId);
    }

    @Override
    public Page<TirePositioningModel> findByVehicleModelId(Long vehicleId, Pageable pageable) {
        return tirePositioningRepository.findByVehicleModelId(vehicleId, pageable);
    }

    @Override
    public Optional<TirePositioningModel> findById(Long id) {
        return tirePositioningRepository.findById(id);
    }

    @Override
    public TirePositioningModel save(TirePositioningModel tirePositioningModel) {
        return tirePositioningRepository.save(tirePositioningModel);
    }

    @Override
    public TirePositioningModel update(TirePositioningModel tirePositioningModel) throws Exception {
        if (tirePositioningModel.getId() == null || !tirePositioningRepository.existsById(tirePositioningModel.getId())) {
            throw new Exception("Tire Positioning not found for update");
        }
        return tirePositioningRepository.save(tirePositioningModel);
    }
}
