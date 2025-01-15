package com.icm.telemetria_peru_api.services;

import com.icm.telemetria_peru_api.dto.VehicleDTO;
import com.icm.telemetria_peru_api.dto.VehicleOptionsDTO;
import com.icm.telemetria_peru_api.mappers.VehicleMapper;
import com.icm.telemetria_peru_api.mappers.VehicleOptionsMapper;
import com.icm.telemetria_peru_api.models.DriverModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.DriverRepository;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final MqttPahoMessageHandler mqttOutbound;
    private final DriverRepository driverRepository;

    private final VehicleMapper vehicleMapper;
    private final VehicleOptionsMapper vehicleOptionsMapper;

    public VehicleDTO findById(Long vehicleId) {
        VehicleModel vehicleModel = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle with id " + vehicleId + " not found"));
        return vehicleMapper.mapToDTO(vehicleModel);

    }

    public List<VehicleDTO> findAll() {
        List<VehicleModel> vehicleModel = vehicleRepository.findAll();
        return vehicleModel.stream()
                .map(vehicleMapper::mapToDTO)
                .toList();
    }

    public List<VehicleDTO> findByCompanyModelId(Long companyId) {
        List<VehicleModel> vehicles = vehicleRepository.findByCompanyModelId(companyId);
        return vehicles.stream()
                .map(vehicleMapper::mapToDTO)
                .toList();
    }

    public Page<VehicleDTO> findByCompanyModelId(Long companyId, Pageable pageable) {
        Page<VehicleModel> vehicleModelsPage = vehicleRepository.findByCompanyModelId(companyId, pageable);
        List<VehicleDTO> vehicleDTOs = vehicleModelsPage.stream()
                .map(vehicleMapper::mapToDTO)
                .toList();
        return new PageImpl<>(vehicleDTOs, pageable, vehicleModelsPage.getTotalElements());
    }

    public List<VehicleDTO> findByStatus(Boolean status) {
        List<VehicleModel> vehicles = vehicleRepository.findByStatus(status);
        return vehicles.stream()
                .map(vehicleMapper::mapToDTO)
                .toList();
    }

    public Page<VehicleDTO> findByStatus(Boolean status, Pageable pageable) {
        Page<VehicleModel> vehicleModelsPage = vehicleRepository.findByStatus(status, pageable);
        List<VehicleDTO> vehicleDTOs = vehicleModelsPage.stream()
                .map(vehicleMapper::mapToDTO)
                .toList();
        return new PageImpl<>(vehicleDTOs, pageable, vehicleModelsPage.getTotalElements());
    }

    public VehicleOptionsDTO findByIdOptions(Long vehicleId) {
        VehicleModel vehicleModel = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle with id " + vehicleId + " not found"));
        return vehicleOptionsMapper.mapToDTO(vehicleModel);

    }

    public VehicleModel save(@Valid VehicleModel vehicleModel) {
        return vehicleRepository.save(vehicleModel);
    }

    public VehicleModel updateMainData(Long vehicleId, @Valid VehicleModel vehicleModel) {
        VehicleModel existing = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + vehicleId + " not found"));

        existing.setLicensePlate(vehicleModel.getLicensePlate());
        existing.setVehicletypeModel(vehicleModel.getVehicletypeModel());
        existing.setCompanyModel(vehicleModel.getCompanyModel());

        return vehicleRepository.save(existing);
    }

    public VehicleModel vehicleOptionsUpdate(Long vehicleId, @Valid String type, @Valid Boolean status) {
        VehicleModel existing = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + vehicleId + " not found"));

        switch (type.toLowerCase()) {
            case "alarm":
                existing.setAlarmStatus(status);
                break;
            case "engine":
                existing.setEngineStatus(status);
                break;
            case "lock":
                existing.setLockStatus(status);
                break;
            default:
                throw new IllegalArgumentException("Invalid type: " + type);
        }

        vehicleRepository.save(existing);

        String topic = "tmp_remoteOptions/" + vehicleId;
        String payload = type + ":" + status;

        mqttOutbound.handleMessage(MessageBuilder.withPayload(payload).setHeader(MqttHeaders.TOPIC, topic).build());

        return existing;
    }

    public VehicleModel statusToggle(Long vehicleId) {
        VehicleModel existing = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + vehicleId + " not found"));
        existing.setStatus(!existing.getStatus());
        return vehicleRepository.save(existing);
    }

    public VehicleModel updateDriver(Long vehicleId, @Valid Long driverId) {
        DriverModel newDriver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver with id " + driverId + " not found"));

        VehicleModel existing = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + vehicleId + " not found"));

        existing.setDriverModel(newDriver);
        return vehicleRepository.save(existing);
    }

    public void deleteById(Long vehicleId) {
        vehicleRepository.deleteById(vehicleId);
    }
}
