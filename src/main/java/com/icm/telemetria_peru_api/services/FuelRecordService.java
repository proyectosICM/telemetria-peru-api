package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.FuelRecordModel;
import com.icm.telemetria_peru_api.repositories.FuelRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class FuelRecordService {
    private final FuelRecordRepository fuelRecordRepository;

    public FuelRecordService(FuelRecordRepository fuelRecordRepository) {
        this.fuelRecordRepository = fuelRecordRepository;
    }

    public List<Map<String, Object>> getHourlyAveragesByDate(LocalDate date, Long vehicleId) {
        return fuelRecordRepository.findHourlyAverageByDate(date, vehicleId);
    }

    public List<Map<String, Object>> findDailyAveragesForLast7Days() {
        return fuelRecordRepository.findDailyAveragesForLast7Days();
    }

    public List<Map<String, Object>> findDailyAveragesForCurrentMonth() {
        return fuelRecordRepository.findDailyAveragesForCurrentMonth();
    }

    public List<Map<String, Object>> findMonthlyAveragesForCurrentYear() {
        return fuelRecordRepository.findMonthlyAveragesForCurrentYear();
    }

    /*********************************/
    /** Starting point for find methods **/
    /*********************************/
    public FuelRecordModel findById(Long id){
        return fuelRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
    }

    public List<FuelRecordModel> findAll(){ return fuelRecordRepository.findAll(); }

    public Page<FuelRecordModel> findAll(Pageable pageable){
        return fuelRecordRepository.findAll(pageable);
    }

    public List<FuelRecordModel> findByVehicleId(Long vehicleId){
        return fuelRecordRepository.findByVehicleModelId(vehicleId);
    }

    public Page<FuelRecordModel> findByVehicleId(Long vehicleId, Pageable pageable){
        return fuelRecordRepository.findByVehicleModelId(vehicleId, pageable);
    }

    /*********************************/
    /** End of find methods section **/
    /*********************************/

    /*********************************/
    /** More CRUD methods **/
    /*********************************/
    public FuelRecordModel save(FuelRecordModel fuelRecordModel){
        return fuelRecordRepository.save(fuelRecordModel);
    }

    public void deleteById(Long id){
        fuelRecordRepository.deleteById(id);
    }
}