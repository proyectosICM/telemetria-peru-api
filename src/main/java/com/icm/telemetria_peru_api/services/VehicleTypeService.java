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
    private final VehicleTypeRepository vehicleTypeRepository;

    @Autowired
    public VehicleTypeService(VehicleTypeRepository vehicleTypeRepository) {
        this.vehicleTypeRepository = vehicleTypeRepository;
    }

    /*********************************/
    /** Starting point for find methods **/
    /*********************************/
    public Optional<VehicleTypeModel> findById(Long id) {
        return vehicleTypeRepository.findById(id);
    }

    public List<VehicleTypeModel> findAll(){
        return vehicleTypeRepository.findAll();
    }

    public Page<VehicleTypeModel> findAll(Pageable pageable){
        return vehicleTypeRepository.findAll(pageable);
    }

    /*********************************/
    /** End of find methods section **/
    /*********************************/

    /*********************************/
    /** More CRUD methods **/
    /*********************************/
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
