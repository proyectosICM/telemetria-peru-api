package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.FuelRecordModel;
import com.icm.telemetria_peru_api.repositories.FuelRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;


public interface FuelRecordService {

    List<Map<String, Object>> getHourlyAveragesByDate(LocalDate date, Long vehicleId);
    List<Map<String, Object>> findDailyAveragesForLast7Days(Long vehicleId);
    List<Map<String, Object>> findDailyAveragesForCurrentMonth(Long vehicleId);
    List<Map<String, Object>> findMonthlyAveragesForCurrentYear(Long vehicleId);
    FuelRecordModel findById(Long id);
    List<FuelRecordModel> findAll();
    Page<FuelRecordModel> findAll(Pageable pageable);
    List<FuelRecordModel> findByVehicleId(Long vehicleId);
    Page<FuelRecordModel> findByVehicleId(Long vehicleId, Pageable pageable);
    FuelRecordModel save(FuelRecordModel fuelRecordModel);
    void deleteById(Long id);
    List<FuelRecordModel> findByVehicleIdAndRange(Long vehicleId, ZonedDateTime start, ZonedDateTime end);
    long countByVehicleId(Long vehicleId);
}