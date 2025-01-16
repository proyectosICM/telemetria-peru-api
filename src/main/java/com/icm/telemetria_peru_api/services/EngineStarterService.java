package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.EngineStarterModel;
import com.icm.telemetria_peru_api.repositories.EngineStarterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EngineStarterService {
    private final EngineStarterRepository engineStarterRepository;

    public Optional<EngineStarterModel> findById(Long id) {
        return engineStarterRepository.findById(id);
    }

    public List<EngineStarterModel> findAll(){
        return engineStarterRepository.findAll();
    }

    public Page<EngineStarterModel> findAll(Pageable pageable){
        return engineStarterRepository.findAll(pageable);
    }

    public List<EngineStarterModel> findByBatteryModelId(Long batteryId) {
        return engineStarterRepository.findByBatteryModelId(batteryId);
    }

    public Page<EngineStarterModel> findByBatteryModelId(Long batteryId, Pageable pageable) {
        return engineStarterRepository.findByBatteryModelId(batteryId, pageable);
    }

    public EngineStarterModel save(EngineStarterModel alternatorModel){
        return engineStarterRepository.save(alternatorModel);
    }
}
