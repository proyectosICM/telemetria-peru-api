package com.icm.telemetria_peru_api.services.impl;

import com.icm.telemetria_peru_api.dto.BatteryDTO;
import com.icm.telemetria_peru_api.mappers.BatteryMapper;
import com.icm.telemetria_peru_api.models.BatteryModel;
import com.icm.telemetria_peru_api.repositories.BatteryRecordRepository;
import com.icm.telemetria_peru_api.repositories.BatteryRepository;
import com.icm.telemetria_peru_api.services.BatteryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BatteryServiceImpl implements BatteryService {
    private final BatteryMapper batteryMapper;
    private final BatteryRepository batteryRepository;
    private final BatteryRecordRepository batteryRecordRepository;

    /*********************************/
    /** Starting point for find methods **/
    /*********************************/
    @Override
    public BatteryDTO findById(Long id) {
        BatteryModel batteryModel = batteryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + id + " not found"));
        return batteryMapper.mapToDTO(batteryModel);
    }

    @Override
    public List<BatteryDTO> findAll() {
        List<BatteryModel> batteryModels = batteryRepository.findAll();
        return batteryModels.stream()
                .map(batteryMapper::mapToDTO)
                .toList();
    }

    @Override
    public Page<BatteryDTO> findAll(Pageable pageable) {
        Page<BatteryModel> batteryModelsPage = batteryRepository.findAll(pageable);
        List<BatteryDTO> batteryDTOs = batteryModelsPage.stream()
                .map(batteryMapper::mapToDTO)
                .toList();
        return new PageImpl<>(batteryDTOs, pageable, batteryModelsPage.getTotalElements());
    }

    @Override
    public List<BatteryDTO> findByVehicleId(Long vehicleId){
        List<BatteryModel> batteryModels = batteryRepository.findByVehicleModelId(vehicleId);
        return batteryModels.stream()
                .map(batteryMapper::mapToDTO)
                .toList();
    }

    @Override
    public Page<BatteryDTO> findByVehicleId(Long vehicleId, Pageable pageable){
        Page<BatteryModel> batteryModelsPage = batteryRepository.findByVehicleModelId(vehicleId, pageable);
        List<BatteryDTO> batteryDTOs = batteryModelsPage.stream()
                .map(batteryMapper::mapToDTO)
                .toList();
        return new PageImpl<>(batteryDTOs, pageable, batteryModelsPage.getTotalElements());
    }

    @Override
    public List<BatteryDTO> findByCompanyId(Long companyId){
        List<BatteryModel> batteryModels = batteryRepository.findByCompanyModelId(companyId);
        return batteryModels.stream()
                .map(batteryMapper::mapToDTO)
                .toList();
    }

    @Override
    public Page<BatteryDTO> findByCompanyId(Long companyId, Pageable pageable){
        Page<BatteryModel> batteryModelsPage = batteryRepository.findByCompanyModelId(companyId, pageable);
        List<BatteryDTO> batteryDTOs = batteryModelsPage.stream()
                .map(batteryMapper::mapToDTO)
                .toList();
        return new PageImpl<>(batteryDTOs, pageable, batteryModelsPage.getTotalElements());
    }

    /*********************************/
    /** End of find methods section **/
    /*********************************/

    /*********************************/
    /** More CRUD methods **/
    /*********************************/
    @Override
    public BatteryModel save(BatteryModel batteryModel){
        return batteryRepository.save(batteryModel);
    }

    @Override
    public BatteryModel update(Long id, BatteryModel batteryModel) {
        BatteryModel existing = batteryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Battery record with id " + id + " not found"));

        existing.setName(batteryModel.getName());

        if (batteryModel.getCompanyModel() != null) {
            existing.setCompanyModel(batteryModel.getCompanyModel());
        }

        if (batteryModel.getVehicleModel() != null) {
            existing.setVehicleModel(batteryModel.getVehicleModel());
        }

        return batteryRepository.save(existing);
    }

    @Override
    public void deleteById(Long id){
        batteryRecordRepository.deleteByBatteryId(id);
        batteryRepository.deleteById(id);
    }
}
