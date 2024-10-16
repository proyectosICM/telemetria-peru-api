package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.DailyLoadCountDTO;
import com.icm.telemetria_peru_api.models.TruckLoadRecordModel;
import com.icm.telemetria_peru_api.repositories.TruckLoadRecordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TruckLoadRecordService {
    private final TruckLoadRecordRepository truckLoadRecordRepository;

    public TruckLoadRecordService(TruckLoadRecordRepository truckLoadRecordRepository){
        this.truckLoadRecordRepository = truckLoadRecordRepository;
    }

    public long countRecordsByVehicleAndDate(Long vehicleId, LocalDate date) {
        return truckLoadRecordRepository.countByVehicleModelIdAndDate(vehicleId, date);
    }

    public Page<Map<String, Object>> getDailyLoadCountsByVehicle(Long vehicleId, Pageable pageable) {
        return truckLoadRecordRepository.findDailyRecordCountsByVehicle(vehicleId, pageable);
    }

    public Optional<TruckLoadRecordModel> findById(Long id) {
        return truckLoadRecordRepository.findById(id);
    }

    public List<TruckLoadRecordModel> findAll(){
        return truckLoadRecordRepository.findAll();
    }

    public Page<TruckLoadRecordModel> findAll(Pageable pageable){
        return truckLoadRecordRepository.findAll(pageable);
    }

    public List<TruckLoadRecordModel> findByVehicleId(Long vehicleId){
        return truckLoadRecordRepository.findByVehicleModelId(vehicleId);
    }

    public Page<TruckLoadRecordModel> findByVehicleId(Long vehicleId, Pageable pageable){
        return truckLoadRecordRepository.findByVehicleModelId(vehicleId, pageable);
    }

    public TruckLoadRecordModel save(TruckLoadRecordModel truckLoadRecordModel){
        return truckLoadRecordRepository.save(truckLoadRecordModel);
    }
}
