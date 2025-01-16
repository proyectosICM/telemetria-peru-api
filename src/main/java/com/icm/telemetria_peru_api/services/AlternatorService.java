package com.icm.telemetria_peru_api.services;

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

    public List<AlternatorModel> findByBatteryModelId(Long batteryId) {
        return alternatorRepository.findByBatteryModelId(batteryId);
    }

    public Page<AlternatorModel> findByBatteryModelId(Long batteryId, Pageable pageable) {
        return alternatorRepository.findByBatteryModelId(batteryId, pageable);
    }

    public AlternatorModel save(AlternatorModel alternatorModel){
        return alternatorRepository.save(alternatorModel);
    }
}
