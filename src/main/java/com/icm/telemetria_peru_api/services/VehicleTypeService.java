package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.VehicleTypeModel;
import com.icm.telemetria_peru_api.repositories.VehicleTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleTypeService {
    @Autowired
    private VehicleTypeRepository vehicletypeRepository;

    public Optional<VehicleTypeModel> findById(Long id) {
        return vehicletypeRepository.findById(id);
    }

    public List<VehicleTypeModel> findAll(){
        return vehicletypeRepository.findAll();
    }

    public Page<VehicleTypeModel> findAll(Pageable pageable){
        return vehicletypeRepository.findAll(pageable);
    }

    public VehicleTypeModel save(VehicleTypeModel vehicletypeModel){
        return vehicletypeRepository.save(vehicletypeModel);
    }

    public VehicleTypeModel update(Long id, VehicleTypeModel vehicletypeModel){
        return vehicletypeRepository.findById(id)
                .map(existing -> {
                    existing.setName(vehicletypeModel.getName());
                    return vehicletypeRepository.save(existing);
                })
                .orElseThrow(() -> new EntityNotFoundException("Vehicletype with id " + id + " not found"));

    }
}
