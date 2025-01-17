package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.AlternatorDTO;
import com.icm.telemetria_peru_api.mappers.AlternatorMapper;
import com.icm.telemetria_peru_api.models.AlternatorModel;
import com.icm.telemetria_peru_api.repositories.AlternatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlternatorService {
    private final AlternatorRepository alternatorRepository;
    private final AlternatorMapper alternatorMapper;

    public List<AlternatorDTO> findAll(){
        List<AlternatorModel> alternatorModels =  alternatorRepository.findAll();
        return alternatorModels.stream()
                .map(alternatorMapper::mapToDTO)
                .toList();
    }

    public Page<AlternatorDTO> findAll(Pageable pageable){
        Page<AlternatorModel> alternatorPage = alternatorRepository.findAll(pageable);
        List<AlternatorDTO> alternatorDTOS = alternatorPage.stream()
                .map(alternatorMapper::mapToDTO)
                .toList();
        return new PageImpl<>(alternatorDTOS, pageable, alternatorPage.getTotalElements());
    }

    public List<AlternatorDTO> findByVehicleModelId(Long vehicleId) {
        List<AlternatorModel> alternatorModels =  alternatorRepository.findByVehicleModelId(vehicleId);
        return alternatorModels.stream()
                .map(alternatorMapper::mapToDTO)
                .toList();
    }

    public Page<AlternatorDTO> findByVehicleModelId(Long vehicleId, Pageable pageable) {
        Page<AlternatorModel> alternatorModelPage =  alternatorRepository.findByVehicleModelId(vehicleId, pageable);
        List<AlternatorDTO> alternatorDTOs = alternatorModelPage.stream()
                .map(alternatorMapper::mapToDTO)
                .toList();
        return new PageImpl<>(alternatorDTOs, pageable, alternatorModelPage.getTotalElements());
    }

    public AlternatorModel save(AlternatorModel alternatorModel){
        return alternatorRepository.save(alternatorModel);
    }
}
