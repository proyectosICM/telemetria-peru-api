package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.VehicleTypeModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface VehicleTypeService {
    Optional<VehicleTypeModel> findById(Long id);
    List<VehicleTypeModel> findAll();
    Page<VehicleTypeModel> findAll(Pageable pageable);
    VehicleTypeModel save(VehicleTypeModel vehicletypeModel);
    VehicleTypeModel update(Long id, VehicleTypeModel vehicletypeModel);
}
