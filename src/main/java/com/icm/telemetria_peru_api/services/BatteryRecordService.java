package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.BatteryRecordDTO;
import com.icm.telemetria_peru_api.mappers.BatteryRecordMapper;
import com.icm.telemetria_peru_api.models.BatteryRecordModel;
import com.icm.telemetria_peru_api.repositories.BatteryRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BatteryRecordService {
    private final BatteryRecordMapper batteryRecordMapper;
    private final BatteryRecordRepository batteryRecordRepository;

    /*********************************/
    /** Starting point for find methods **/
    /*********************************/
    public BatteryRecordDTO findById(Long id){
        BatteryRecordModel batteryRecordModel = batteryRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
        return batteryRecordMapper.mapToDTO(batteryRecordModel);
    }

    public List<BatteryRecordDTO> findAll(){
        List<BatteryRecordModel> batteryRecordModel = batteryRecordRepository.findAll();
        return batteryRecordModel.stream()
                .map(batteryRecordMapper::mapToDTO)
                .toList();
    }

    public Page<BatteryRecordDTO> findAll(Pageable pageable){
        Page<BatteryRecordModel> batteryRecordModelsPage = batteryRecordRepository.findAll(pageable);
        List<BatteryRecordDTO> batteryRecordDTOs = batteryRecordModelsPage.stream()
                .map(batteryRecordMapper::mapToDTO)
                .toList();
        return new PageImpl<>(batteryRecordDTOs, pageable, batteryRecordModelsPage.getTotalElements());
    }

    public List<BatteryRecordDTO> findByBatteryId(Long batteryId){
        List<BatteryRecordModel> batteryRecordModels = batteryRecordRepository.findByBatteryModelId(batteryId);
        return batteryRecordModels.stream()
                .map(batteryRecordMapper::mapToDTO)
                .toList();
    }

    public Page<BatteryRecordDTO> findByBatteryId(Long batteryId, Pageable pageable){
        Page<BatteryRecordModel> batteryRecordModelsPage = batteryRecordRepository.findByBatteryModelId(batteryId, pageable);
        List<BatteryRecordDTO> batteryRecordDTOs = batteryRecordModelsPage.stream()
                .map(batteryRecordMapper::mapToDTO)
                .toList();
        return new PageImpl<>(batteryRecordDTOs, pageable, batteryRecordModelsPage.getTotalElements());
    }

    /*********************************/
    /** End of find methods section **/
    /*********************************/

    /*********************************/
    /** More CRUD methods **/
    /*********************************/
    public BatteryRecordModel save(BatteryRecordModel batteryRecordModel){
        return batteryRecordRepository.save(batteryRecordModel);
    }
}