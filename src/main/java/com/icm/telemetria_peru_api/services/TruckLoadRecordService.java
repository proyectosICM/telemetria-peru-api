package com.icm.telemetria_peru_api.services;


import com.icm.telemetria_peru_api.models.TruckLoadRecordModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TruckLoadRecordService {
    long countRecordsByVehicleAndDate(Long vehicleId, LocalDate date);
    List<Map<String, Object>> getDataMonth(Long vehicleId, Integer year, Integer month);
    Page<Map<String, Object>> getDailyLoadCountsByVehicle(Long vehicleId, Pageable pageable);
    Optional<TruckLoadRecordModel> findById(Long id);
    List<TruckLoadRecordModel> findAll();
    Page<TruckLoadRecordModel> findAll(Pageable pageable);
    List<TruckLoadRecordModel> findByVehicleId(Long vehicleId);
    Page<TruckLoadRecordModel> findByVehicleId(Long vehicleId, Pageable pageable);
    TruckLoadRecordModel save(TruckLoadRecordModel truckLoadRecordModel);

}
