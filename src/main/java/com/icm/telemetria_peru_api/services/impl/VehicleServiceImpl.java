package com.icm.telemetria_peru_api.services.impl;

import com.icm.telemetria_peru_api.dto.VehicleDTO;
import com.icm.telemetria_peru_api.dto.VehicleOptionsDTO;
import com.icm.telemetria_peru_api.dto.VehicleVideoDTO;
import com.icm.telemetria_peru_api.integration.dvr.DvrCommandClient;
import com.icm.telemetria_peru_api.mappers.VehicleMapper;
import com.icm.telemetria_peru_api.mappers.VehicleOptionsMapper;
import com.icm.telemetria_peru_api.enums.GpsSource;
import com.icm.telemetria_peru_api.models.DriverModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.DriverRepository;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import com.icm.telemetria_peru_api.services.VehicleService;
import com.icm.telemetria_peru_api.utils.DvrPhoneNormalizer;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final MqttPahoMessageHandler mqttOutbound;
    private final DriverRepository driverRepository;
    private final DvrCommandClient dvrCommandClient;

    private final VehicleMapper vehicleMapper;
    private final VehicleOptionsMapper vehicleOptionsMapper;

    @Value("${HLS_BASE_URL}")
    private String hlsBaseUrl;

    @Override
    public List<VehicleDTO> findAll() {
        List<VehicleModel> vehicleModel = vehicleRepository.findAll();
        return vehicleModel.stream()
                .map(vehicleMapper::mapToDTO)
                .toList();
    }

    @Override
    public VehicleDTO findById(Long vehicleId) {
        VehicleModel vehicleModel = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle with id " + vehicleId + " not found"));
        return vehicleMapper.mapToDTO(vehicleModel);

    }

    @Override
    public List<VehicleDTO> findByCompanyModelId(Long companyId) {
        List<VehicleModel> vehicles = vehicleRepository.findByCompanyModelId(companyId);
        return vehicles.stream()
                .map(vehicleMapper::mapToDTO)
                .toList();
    }

    @Override
    public Page<VehicleDTO> findByCompanyModelId(Long companyId, Pageable pageable) {
        Page<VehicleModel> vehicleModelsPage = vehicleRepository.findByCompanyModelId(companyId, pageable);
        List<VehicleDTO> vehicleDTOs = vehicleModelsPage.stream()
                .map(vehicleMapper::mapToDTO)
                .toList();
        return new PageImpl<>(vehicleDTOs, pageable, vehicleModelsPage.getTotalElements());
    }

    @Override
    public List<VehicleDTO> findByStatus(Boolean status) {
        List<VehicleModel> vehicles = vehicleRepository.findByStatus(status);
        return vehicles.stream()
                .map(vehicleMapper::mapToDTO)
                .toList();
    }

    @Override
    public Page<VehicleDTO> findByStatus(Boolean status, Pageable pageable) {
        Page<VehicleModel> vehicleModelsPage = vehicleRepository.findByStatus(status, pageable);
        List<VehicleDTO> vehicleDTOs = vehicleModelsPage.stream()
                .map(vehicleMapper::mapToDTO)
                .toList();
        return new PageImpl<>(vehicleDTOs, pageable, vehicleModelsPage.getTotalElements());
    }

    @Override
    public VehicleOptionsDTO findByIdOptions(Long vehicleId) {
        VehicleModel vehicleModel = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle with id " + vehicleId + " not found"));
        return vehicleOptionsMapper.mapToDTO(vehicleModel);

    }

    @Override
    public VehicleVideoDTO getVideoConfig(Long vehicleId) {
        VehicleModel v = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle with id " + vehicleId + " not found"));

        VehicleVideoDTO dto = new VehicleVideoDTO();
        dto.setVehicleId(v.getId());
        dto.setLicensePlate(v.getLicensePlate());
        dto.setVideoChannels(v.getVideoChannels());

        // --- Normalizar el dvrPhone ---
        final String normalizedPhone = DvrPhoneNormalizer.normalize(v.getDvrPhone());
        dto.setDvrPhone(normalizedPhone);

        // Si no hay phone normalizado o no hay canales, devolvemos lista vacía
        if (normalizedPhone == null ||
                v.getVideoChannels() == null ||
                v.getVideoChannels().isEmpty()) {

            dto.setHlsUrls(java.util.Collections.emptyList());
            return dto;
        }

        // Construir URLs tipo: HLS_BASE_URL/0000PHONE_CHANNEL.m3u8
        java.util.List<String> urls = v.getVideoChannels().stream()
                .sorted() // opcional, solo para que salgan ordenados
                .map(ch -> String.format("%s/%s_%d.m3u8", hlsBaseUrl, normalizedPhone, ch))
                .toList(); // o collect(Collectors.toList()) si tu versión de Java no soporta toList()

        dto.setHlsUrls(urls);
        return dto;
    }

    @Override
    public void ensureVideoStream(Long vehicleId, Integer channel) {
        String normalizedPhone = getNormalizedDvrPhone(vehicleId);
        validateChannel(channel);
        dvrCommandClient.ensureVideoStream(normalizedPhone, channel);
    }

    @Override
    public void stopVideoStream(Long vehicleId, Integer channel) {
        String normalizedPhone = getNormalizedDvrPhone(vehicleId);
        validateChannel(channel);
        dvrCommandClient.stopVideoStream(normalizedPhone, channel);
    }

    @Override
    public VehicleModel save(@Valid VehicleModel vehicleModel) {
        normalizeVehicleTelemetryConfig(vehicleModel);

        if (vehicleModel.getDvrPhone() == null || vehicleModel.getDvrPhone().isBlank()) {
            if (vehicleModel.getVideoChannels() != null) {
                vehicleModel.getVideoChannels().clear();
            }
        }

        return vehicleRepository.save(vehicleModel);
    }

    @Override
    public VehicleModel updateMainData(Long vehicleId, @Valid VehicleModel vehicleModel) {
        VehicleModel existing = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + vehicleId + " not found"));

        // Datos "clásicos"
        existing.setImei(vehicleModel.getImei());
        existing.setLicensePlate(vehicleModel.getLicensePlate());
        existing.setVehicletypeModel(vehicleModel.getVehicletypeModel());
        existing.setCompanyModel(vehicleModel.getCompanyModel());
        existing.setMaxSpeed(vehicleModel.getMaxSpeed());
        existing.setFuelType(vehicleModel.getFuelType());

        // --- DVR / Video ---
        existing.setDvrPhone(DvrPhoneNormalizer.normalize(vehicleModel.getDvrPhone()));
        existing.setGpsSource(resolveGpsSource(vehicleModel));

        existing.getVideoChannels().clear();
        if (vehicleModel.getVideoChannels() != null) {
            existing.getVideoChannels().addAll(vehicleModel.getVideoChannels());
        }

        // si no hay dvrPhone, no tiene sentido dejar canales configurados
        if (existing.getDvrPhone() == null || existing.getDvrPhone().isBlank()) {
            existing.getVideoChannels().clear();
        }

        return vehicleRepository.save(existing);
    }

    @Override
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

    @Override
    public VehicleModel statusToggle(Long vehicleId) {
        VehicleModel existing = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + vehicleId + " not found"));
        existing.setStatus(!existing.getStatus());
        return vehicleRepository.save(existing);
    }

    @Override
    public VehicleModel updateDriver(Long vehicleId, @Valid Long driverId) {
        DriverModel newDriver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver with id " + driverId + " not found"));

        VehicleModel existing = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Record with id " + vehicleId + " not found"));

        existing.setDriverModel(newDriver);
        return vehicleRepository.save(existing);
    }

    @Override
    public void deleteById(Long vehicleId) {
        vehicleRepository.deleteById(vehicleId);
    }

    private String getNormalizedDvrPhone(Long vehicleId) {
        VehicleModel vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle with id " + vehicleId + " not found"));
        String normalizedPhone = DvrPhoneNormalizer.normalize(vehicle.getDvrPhone());
        if (normalizedPhone == null) {
            throw new IllegalArgumentException("El vehiculo no tiene dvrPhone configurado");
        }
        return normalizedPhone;
    }

    private void validateChannel(Integer channel) {
        if (channel == null || channel < 1 || channel > 8) {
            throw new IllegalArgumentException("Canal de video invalido");
        }
    }

    private void normalizeVehicleTelemetryConfig(VehicleModel vehicleModel) {
        vehicleModel.setDvrPhone(DvrPhoneNormalizer.normalize(vehicleModel.getDvrPhone()));
        vehicleModel.setGpsSource(resolveGpsSource(vehicleModel));
    }

    private GpsSource resolveGpsSource(VehicleModel vehicleModel) {
        if (vehicleModel.getGpsSource() != null) {
            return vehicleModel.getGpsSource();
        }

        boolean hasImei = vehicleModel.getImei() != null && !vehicleModel.getImei().isBlank();
        boolean hasDvr = vehicleModel.getDvrPhone() != null && !vehicleModel.getDvrPhone().isBlank();

        if (hasDvr && !hasImei) {
            return GpsSource.DVR;
        }
        if (hasImei && !hasDvr) {
            return GpsSource.IMEI;
        }
        return GpsSource.AUTO;
    }
}
