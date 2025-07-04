package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.VehicleDTO;
import com.icm.telemetria_peru_api.dto.VehicleOptionsDTO;
import com.icm.telemetria_peru_api.models.VehicleModel;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VehicleService {
    List<VehicleDTO> findAll();
    VehicleDTO findById(Long vehicleId);
    List<VehicleDTO> findByCompanyModelId(Long companyId);
    Page<VehicleDTO> findByCompanyModelId(Long companyId, Pageable pageable);
    List<VehicleDTO> findByStatus(Boolean status);
    Page<VehicleDTO> findByStatus(Boolean status, Pageable pageable);
    VehicleOptionsDTO findByIdOptions(Long vehicleId);
    VehicleModel save(@Valid VehicleModel vehicleModel);
    VehicleModel updateMainData(Long vehicleId, @Valid VehicleModel vehicleModel);
    VehicleModel vehicleOptionsUpdate(Long vehicleId, @Valid String type, @Valid Boolean status);
    VehicleModel statusToggle(Long vehicleId);
    VehicleModel updateDriver(Long vehicleId, @Valid Long driverId);
    void deleteById(Long vehicleId);
}
