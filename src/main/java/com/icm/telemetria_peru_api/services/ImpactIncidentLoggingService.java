package com.icm.telemetria_peru_api.services;


import com.icm.telemetria_peru_api.models.ImpactIncidentLoggingModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ImpactIncidentLoggingService {
    List<ImpactIncidentLoggingModel> findAll();
    Page<ImpactIncidentLoggingModel> findAll(Pageable pageable);
    ImpactIncidentLoggingModel findById(Long id);
    List<ImpactIncidentLoggingModel> findByVehicleId(Long vehicleId);
    Page<ImpactIncidentLoggingModel> findByVehicleId(Long vehicleId, Pageable pageable);
    ImpactIncidentLoggingModel save(ImpactIncidentLoggingModel impactIncidentLoggingModel);
    void sendEmailInfo(Long vehicleId);
    void deleteById(Long id);
}
