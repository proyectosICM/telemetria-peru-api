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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final MqttPahoMessageHandler mqttOutbound;
    private final DriverRepository driverRepository;

    private final VehicleMapper vehicleMapper;
    private final VehicleOptionsMapper vehicleOptionsMapper;

    /*********************************/
    /** Starting point for find methods **/
    /*********************************/
    public VehicleDTO findById(Long vehicleId) {
        VehicleModel vehicleModel = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle with id " + vehicleId + " not found"));
        return vehicleMapper.mapToDTO(vehicleModel);

    }

    public VehicleOptionsDTO findByIdOptions(Long vehicleId) {
        VehicleModel vehicleModel = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle with id " + vehicleId + " not found"));
        return vehicleOptionsMapper.mapToDTO(vehicleModel);

    }

    /**
     * Retrieves vehicles, as a list and paginated.
     */
    public List<VehicleDTO> findAll() {
        List<VehicleModel> vehicleModel = vehicleRepository.findAll();
        return vehicleModel.stream()
                .map(vehicleMapper::mapToDTO)
                .toList();
    }

    public Page<VehicleDTO> findAll(Pageable pageable) {
        Page<VehicleModel> vehicleModelsPage = vehicleRepository.findAll(pageable);
        List<VehicleDTO> vehicleDTOs = vehicleModelsPage.stream()
                .map(vehicleMapper::mapToDTO)
                .toList();
        return new PageImpl<>(vehicleDTOs, pageable, vehicleModelsPage.getTotalElements());
    }

    /**
     * Retrieves vehicles by status, as a list and paginated.
     */
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

    /**
     * Retrieves vehicles by vehicleType, as a list and paginated.
     */
    public List<VehicleDTO> findByVehicleTypeModelId(Long vehicleTypeId) {
        List<VehicleModel> vehicles = vehicleRepository.findByVehicletypeModelId(vehicleTypeId);
        return vehicles.stream()
                .map(vehicleMapper::mapToDTO)
                .toList();
    }

    public Page<VehicleDTO> findByVehicleTypeModelId(Long vehicleTypeId, Pageable pageable) {
        Page<VehicleModel> vehicleModelsPage = vehicleRepository.findByVehicletypeModelId(vehicleTypeId, pageable);
        List<VehicleDTO> vehicleDTOs = vehicleModelsPage.stream()
                .map(vehicleMapper::mapToDTO)
                .toList();
        return new PageImpl<>(vehicleDTOs, pageable, vehicleModelsPage.getTotalElements());
    }

    /**
     * Retrieves vehicles by company, as a list and paginated.
     */
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

    /**
     * Retrieves vehicles by vehicleType and company, as a list and paginated.
     */
    public List<VehicleDTO> findByVehicleTypeModelIdAndCompanyModelId(Long vehicleTypeId, Long companyId) {
        List<VehicleModel> vehicles = vehicleRepository.findByVehicletypeModelIdAndCompanyModelId(vehicleTypeId, companyId);
        return vehicles.stream()
                .map(vehicleMapper::mapToDTO)
                .toList();
    }

    public Page<VehicleDTO> findByVehicleTypeModelIdAndCompanyModelId(Long vehicleTypeId, Long companyId, Pageable pageable) {
        Page<VehicleModel> vehicleModelsPage = vehicleRepository.findByVehicletypeModelIdAndCompanyModelId(vehicleTypeId, companyId, pageable);
        List<VehicleDTO> vehicleDTOs = vehicleModelsPage.stream()
                .map(vehicleMapper::mapToDTO)
                .toList();
        return new PageImpl<>(vehicleDTOs, pageable, vehicleModelsPage.getTotalElements());
    }

    /**
     * Retrieves vehicles by vehicleType and compan and status, as a list and paginated.
     */
    public List<VehicleDTO> findByVehicleTypeModelIdAndCompanyModelIdAndStatus(Long vehicleTypeId, Long companyId, Boolean status) {
        List<VehicleModel> vehicles = vehicleRepository.findByVehicletypeModelIdAndCompanyModelIdAndStatus(vehicleTypeId, companyId, status);
        return vehicles.stream()
                .map(vehicleMapper::mapToDTO)
                .toList();
    }

    public Page<VehicleDTO> findByVehicleTypeModelIdAndCompanyModelIdAndStatus(Long vehicleTypeId, Long companyId, Boolean status, Pageable pageable) {
        Page<VehicleModel> vehicleModelsPage = vehicleRepository.findByVehicletypeModelIdAndCompanyModelIdAndStatus(vehicleTypeId, companyId, status, pageable);
        List<VehicleDTO> vehicleDTOs = vehicleModelsPage.stream()
                .map(vehicleMapper::mapToDTO)
                .toList();
        return new PageImpl<>(vehicleDTOs, pageable, vehicleModelsPage.getTotalElements());
    }

    /*********************************/
    /** End of find methods section **/
    /*********************************/

    /*********************************/
    /** More CRUD methods **/
    /*********************************/
    public VehicleModel save(@Valid VehicleModel vehicleModel) {
        return vehicleRepository.save(vehicleModel);
    }

    /**
     * Main data update
     */
    public VehicleModel updateMainData(Long vehicleId, @Valid VehicleModel vehicleModel) {
        VehicleModel existing = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + vehicleId + " not found"));

        // Actualiza los campos necesarios
        existing.setLicensePlate(vehicleModel.getLicensePlate());
        existing.setVehicletypeModel(vehicleModel.getVehicletypeModel());
        existing.setCompanyModel(vehicleModel.getCompanyModel());

        // Guarda el objeto actualizado
        return vehicleRepository.save(existing);
    }

    /**
     * Change vehicle driver
     */
    public VehicleModel changeDriver(Long vehicleId, @Valid Long driverId) {
        DriverModel newDriver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver with id " + driverId + " not found"));

        VehicleModel existing = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + vehicleId + " not found"));

        existing.setDriverModel(newDriver);
        return vehicleRepository.save(existing);
    }

    /**
     * Update vehicle options
     */
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

        // Publicar el estado actualizado en un tema específico
        String topic = "tmp_remoteOptions";
        String payload = type + ":" + status;

        mqttOutbound.handleMessage(MessageBuilder.withPayload(payload).setHeader(MqttHeaders.TOPIC, topic).build());

        return existing;
    }

    /**
     * Update vehicle time on
     */
    public VehicleModel timeOnUpdate(Long vehicleId, @Valid Long timeOn) {
        VehicleModel existing = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + vehicleId + " not found"));
        existing.setTimeOn(timeOn);
        return vehicleRepository.save(existing);
    }

    /**
     * Toggle vehicle status
     */
    public VehicleModel changeStatus(Long vehicleId) {
        VehicleModel existing = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + vehicleId + " not found"));
        existing.setStatus(!existing.getStatus());
        return vehicleRepository.save(existing);
    }

    public void deleteById(Long vehicleId) {
        vehicleRepository.deleteById(vehicleId);
    }
}
