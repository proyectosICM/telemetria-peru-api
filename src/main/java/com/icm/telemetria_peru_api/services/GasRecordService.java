package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.GasRecordModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GasRecordService {
    Optional<GasRecordModel> findById(Long id);
    List<GasRecordModel> findByVehicleId(Long vehicleId);
    Page<GasRecordModel> findByVehicleId(Long vehicleId, Pageable pageable);
    List<GasRecordModel> findByVehicleIdOrdered(Long vehicleId);
    List<GasRecordModel> findTodayByVehicleId(Long vehicleId);
    List<GasRecordModel> findByVehicleIdAndDate(Long vehicleId, String viewType, int year, Integer month, Integer day);
    GasRecordModel save(GasRecordModel gasRecordModel);
    void deleteById(Long id);
}
