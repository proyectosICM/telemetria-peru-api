package com.icm.telemetria_peru_api.services.impl;

import com.icm.telemetria_peru_api.models.FuelRecordModel;
import com.icm.telemetria_peru_api.repositories.FuelRecordRepository;
import com.icm.telemetria_peru_api.services.FuelRecordService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FuelRecordServiceImpl implements FuelRecordService {
    private final FuelRecordRepository fuelRecordRepository;

    @Override
    public List<Map<String, Object>> getHourlyAveragesByDate(LocalDate date, Long vehicleId) {
        return fuelRecordRepository.findHourlyAverageByDate(date, vehicleId);
    }

    @Override
    public List<Map<String, Object>> findDailyAveragesForLast7Days(Long vehicleId) {
        return fuelRecordRepository.findDailyAveragesForLast7Days(vehicleId);
    }

    @Override
    public List<Map<String, Object>> findDailyAveragesForCurrentMonth(Long vehicleId) {
        return fuelRecordRepository.findDailyAveragesForCurrentMonth(vehicleId);
    }

    @Override
    public List<Map<String, Object>> findMonthlyAveragesForCurrentYear(Long vehicleId) {
        return fuelRecordRepository.findMonthlyAveragesForCurrentYear(vehicleId);
    }

    @Override
    /*********************************/
    /** Starting point for find methods **/
    /*********************************/
    public FuelRecordModel findById(Long id){
        return fuelRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
    }

    @Override
    public List<FuelRecordModel> findAll(){ return fuelRecordRepository.findAll(); }

    @Override
    public Page<FuelRecordModel> findAll(Pageable pageable){
        return fuelRecordRepository.findAll(pageable);
    }

    @Override
    public List<FuelRecordModel> findByVehicleId(Long vehicleId){
        return fuelRecordRepository.findByVehicleModelId(vehicleId);
    }

    @Override
    public Page<FuelRecordModel> findByVehicleId(Long vehicleId, Pageable pageable){
        return fuelRecordRepository.findByVehicleModelId(vehicleId, pageable);
    }

    /*********************************/
    /** End of find methods section **/
    /*********************************/

    /*********************************/
    /** More CRUD methods **/
    /*********************************/
    @Override
    public FuelRecordModel save(FuelRecordModel fuelRecordModel){
        return fuelRecordRepository.save(fuelRecordModel);
    }

    @Override
    public void deleteById(Long id){
        fuelRecordRepository.deleteById(id);
    }
}
