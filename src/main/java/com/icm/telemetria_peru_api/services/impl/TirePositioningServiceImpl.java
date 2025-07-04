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
    public List<TirePositioningModel> findByVehicleId(Long vehicleId) {
        return tirePositioningRepository.findByVehicleId(vehicleId);
    }

    @Override
    public Page<TirePositioningModel> findByVehicleId(Long vehicleId, Pageable pageable) {
        return tirePositioningRepository.findByVehicleId(vehicleId, pageable);
    }

    @Override
    public Optional<TirePositioningModel> findById(Long id) {
        return tirePositioningRepository.findById(id);
    }
}
