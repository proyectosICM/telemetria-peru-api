package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.IgnitionDuration;
import com.icm.telemetria_peru_api.models.VehicleIgnitionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface VehicleIgnitionService {
    Optional<VehicleIgnitionModel> findById(Long id);
    List<VehicleIgnitionModel> findAll();
    Page<VehicleIgnitionModel> findAll(Pageable pageable);
    List<VehicleIgnitionModel> findByVehicleModelId(Long vehicleId);
    Page<VehicleIgnitionModel> findByVehicleModelId(Long vehicleId, Pageable pageable);
    List<IgnitionDuration> calculateActiveDurations(Long vehicleId);
    Map<String, Object> getCounts(Long vehicleId);
    List<Map<String, Object>> getIgnitionCountsByMonth(Long vehicleId, Integer year);
    List<Map<String, Object>> getCountByMonth(Long vehicleId, Integer year, Integer month);
    VehicleIgnitionModel save(VehicleIgnitionModel vehicleIgnitionModel);
}
