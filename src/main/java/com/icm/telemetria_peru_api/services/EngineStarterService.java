package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.EngineStarterDTO;
import com.icm.telemetria_peru_api.mappers.EngineStarterMapper;
import com.icm.telemetria_peru_api.models.EngineStarterModel;
import com.icm.telemetria_peru_api.repositories.EngineStarterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EngineStarterService {
    private final EngineStarterRepository engineStarterRepository;
    private final EngineStarterMapper engineStarterMapper;

    public List<EngineStarterDTO> findAll(){
        List<EngineStarterModel> engineStarterModels = engineStarterRepository.findAll();
        return engineStarterModels.stream()
                .map(engineStarterMapper::mapToDTO)
                .toList();
    }

    public Page<EngineStarterDTO> findAll(Pageable pageable){
        Page<EngineStarterModel> engineStarterModelPage = engineStarterRepository.findAll(pageable);
        List<EngineStarterDTO> engineStarterDTOs = engineStarterModelPage.stream()
                .map(engineStarterMapper::mapToDTO)
                .toList();
        return new PageImpl<>(engineStarterDTOs, pageable, engineStarterModelPage.getTotalElements());
    }

    public List<EngineStarterDTO> findByVehicleModelId(Long vehicleId) {
        List<EngineStarterModel> engineStarterModels = engineStarterRepository.findByVehicleModelId(vehicleId);
        return engineStarterModels.stream()
                .map(engineStarterMapper::mapToDTO)
                .toList();
    }

    public Page<EngineStarterDTO> findByVehicleModelId(Long vehicleId, Pageable pageable) {
        Page<EngineStarterModel> engineStarterModels = engineStarterRepository.findByVehicleModelId(vehicleId, pageable);
        List<EngineStarterDTO> engineStarterDTOS = engineStarterModels.stream()
                .map(engineStarterMapper::mapToDTO)
                .toList();
        return new PageImpl<>(engineStarterDTOS, pageable, engineStarterModels.getTotalElements());
    }

    public EngineStarterModel save(EngineStarterModel alternatorModel){
        return engineStarterRepository.save(alternatorModel);
    }
}
