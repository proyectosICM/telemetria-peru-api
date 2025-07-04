package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.SpeedExcessLoggerModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SpeedExcessLoggerService {
    List<SpeedExcessLoggerModel> findAll();
    Page<SpeedExcessLoggerModel> findAll(Pageable pageable);
    SpeedExcessLoggerModel findById(Long id);
    List<SpeedExcessLoggerModel> findByVehicleId(Long vehicleId);
    Page<SpeedExcessLoggerModel> findByVehicleId(Long vehicleId, Pageable pageable);
    SpeedExcessLoggerModel save(SpeedExcessLoggerModel speedExcessLoggerModel);
    void sendEmailInfo(Long vehicleId);
    void deleteById(Long id);
}
