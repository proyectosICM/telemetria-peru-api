package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.models.GasRecordModel;
import com.icm.telemetria_peru_api.repositories.GasRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GasRecordService {
    private final GasRecordRepository gasRecordRepository;

    public GasRecordService(GasRecordRepository gasRecordRepository) {
        this.gasRecordRepository = gasRecordRepository;
    }

    /*********************************/
    /** Starting point for find methods **/
    /*********************************/
    public GasRecordModel findById(Long id){
        return gasRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
    }

    public List<GasRecordModel> findAll(){ return gasRecordRepository.findAll(); }

    public Page<GasRecordModel> findAll(Pageable pageable){
        return gasRecordRepository.findAll(pageable);
    }

    public List<GasRecordModel> findByVehicleId(Long vehicleId){
        return gasRecordRepository.findByVehicleModelId(vehicleId);
    }

    public Page<GasRecordModel> findByVehicleId(Long vehicleId, Pageable pageable){
        return gasRecordRepository.findByVehicleModelId(vehicleId, pageable);
    }

    /*********************************/
    /** End of find methods section **/
    /*********************************/

    /*********************************/
    /** More CRUD methods **/
    /*********************************/
    public GasRecordModel save(GasRecordModel gasRecordModel){
        return gasRecordRepository.save(gasRecordModel);
    }

    public void deleteById(Long id){
        gasRecordRepository.deleteById(id);
    }
}