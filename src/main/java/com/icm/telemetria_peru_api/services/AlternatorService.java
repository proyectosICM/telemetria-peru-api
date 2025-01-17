package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.AlternatorDTO;
import com.icm.telemetria_peru_api.models.AlternatorModel;
import com.icm.telemetria_peru_api.repositories.AlternatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlternatorService {
    private final AlternatorRepository alternatorRepository;

    public Optional<AlternatorModel> findById(Long id) {
        return alternatorRepository.findById(id);
    }

    public List<AlternatorModel> findAll(){
        return alternatorRepository.findAll();
    }

    public Page<AlternatorModel> findAll(Pageable pageable){
        return alternatorRepository.findAll(pageable);
    }

    public List<AlternatorDTO> findByVehicleModelId(Long vehicleId) {
        return alternatorRepository.findByVehicleModelId(vehicleId);
    }

    public Page<AlternatorDTO> findByVehicleModelId(Long vehicleId, Pageable pageable) {
        return alternatorRepository.findByVehicleModelId(vehicleId, pageable);
    }

    public AlternatorModel save(AlternatorModel alternatorModel){
        return alternatorRepository.save(alternatorModel);
    }
}
