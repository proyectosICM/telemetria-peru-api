package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.VehicletypeModel;
import com.icm.telemetria_peru_api.repositories.VehicletypeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicletypeService {
    @Autowired
    private VehicletypeRepository vehicletypeRepository;

    public Optional<VehicletypeModel> findById(Long id) {
        return vehicletypeRepository.findById(id);
    }

    public List<VehicletypeModel> findAll(){
        return vehicletypeRepository.findAll();
    }

    public Page<VehicletypeModel> findAll(Pageable pageable){
        return vehicletypeRepository.findAll(pageable);
    }

    public VehicletypeModel save(VehicletypeModel vehicletypeModel){
        return vehicletypeRepository.save(vehicletypeModel);
    }

    public VehicletypeModel update(Long id, VehicletypeModel vehicletypeModel){
        return vehicletypeRepository.findById(id)
                .map(existing -> {
                    existing.setName(vehicletypeModel.getName());
                    return vehicletypeRepository.save(existing);
                })
                .orElseThrow(() -> new EntityNotFoundException("Vehicletype with id " + id + " not found"));

    }
}
