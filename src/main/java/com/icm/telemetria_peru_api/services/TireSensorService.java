package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.TireSensorModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TireSensorService {
    List<TireSensorModel> findAll();
    Page<TireSensorModel> findAll(Pageable pageable);
    TireSensorModel findById(Long id);
    Optional<TireSensorModel> findByIdentificationCode(String code);
    List<TireSensorModel> findByCompanyModelId(Long companyId);
    Page<TireSensorModel> findByCompanyModelId(Long companyId, Pageable pageable);
    List<TireSensorModel> findByVehicleModelId(Long vehicleId);
    Page<TireSensorModel> findByVehicleModelId(Long vehicleId, Pageable pageable);
    List<TireSensorModel> findByStatus(Boolean status);
    Page<TireSensorModel> findByStatus(Boolean status, Pageable pageable);
    List<TireSensorModel> findByVehicleModelIdAndStatus(Long vehicleId, Boolean status);
    Page<TireSensorModel> findByVehicleModelIdAndStatusPage(Long vehicleId, Boolean status, Pageable pageable);
    TireSensorModel save(TireSensorModel tireSensorModel);
    TireSensorModel update(Long id, TireSensorModel tireSensorModel);
    void deleteById(Long id);
}
