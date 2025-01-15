package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.VehicleTypeModel;
import com.icm.telemetria_peru_api.repositories.VehicleTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleTypeService {
    private final VehicleTypeRepository vehicleTypeRepository;

    public Optional<VehicleTypeModel> findById(Long id) {
        return vehicleTypeRepository.findById(id);
    }

    public List<VehicleTypeModel> findAll(){
        return vehicleTypeRepository.findAll();
    }

    public Page<VehicleTypeModel> findAll(Pageable pageable){
        return vehicleTypeRepository.findAll(pageable);
    }

    public VehicleTypeModel save(VehicleTypeModel vehicletypeModel){
        return vehicleTypeRepository.save(vehicletypeModel);
    }

    public VehicleTypeModel update(Long id, VehicleTypeModel vehicletypeModel){
        return vehicleTypeRepository.findById(id)
                .map(existing -> {
                    existing.setName(vehicletypeModel.getName());
                    return vehicleTypeRepository.save(existing);
                })
                .orElseThrow(() -> new EntityNotFoundException("VehicleType with id " + id + " not found"));
    }
}
