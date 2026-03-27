package com.icm.telemetria_peru_api.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icm.telemetria_peru_api.dto.*;
import com.icm.telemetria_peru_api.integration.dvr.DvrCommandClient;
import com.icm.telemetria_peru_api.models.DvrAlertExecutionModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.DvrAlertExecutionRepository;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import com.icm.telemetria_peru_api.services.DvrAlertService;
import com.icm.telemetria_peru_api.utils.DvrPhoneNormalizer;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DvrAlertServiceImpl implements DvrAlertService {
    private final VehicleRepository vehicleRepository;
    private final DvrAlertExecutionRepository dvrAlertExecutionRepository;
    private final DvrCommandClient dvrCommandClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public VehicleDvrAlertsDTO getVehicleAlerts(Long vehicleId) {
        VehicleModel vehicle = getVehicle(vehicleId);
        String normalizedPhone = DvrPhoneNormalizer.normalize(vehicle.getDvrPhone());

        VehicleDvrAlertsDTO dto = new VehicleDvrAlertsDTO();
        dto.setVehicleId(vehicle.getId());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setDvrPhone(normalizedPhone);

        if (normalizedPhone == null) {
            dto.setOnline(false);
            dto.setAlerts(List.of());
            return dto;
        }

        JsonNode response = dvrCommandClient.getAlerts(normalizedPhone);
        dto.setOnline(response.path("online").asBoolean(false));
        dto.setConnectedAt(nullIfMissing(response, "connectedAt"));
        dto.setLastSeenAt(nullIfMissing(response, "lastSeenAt"));
        dto.setAlerts(mapAlerts(response.path("alerts")));
        return dto;
    }

    @Override
    public DvrAlertExecutionDTO executeAlert(Long vehicleId, DvrAlertExecuteRequestDTO request, String requestedBy) {
        VehicleModel vehicle = getVehicle(vehicleId);
        String normalizedPhone = DvrPhoneNormalizer.normalize(vehicle.getDvrPhone());
        if (normalizedPhone == null) {
            throw new IllegalArgumentException("El vehiculo no tiene dvrPhone configurado");
        }

        DvrAlertExecutionModel execution = new DvrAlertExecutionModel();
        execution.setVehicleModel(vehicle);
        execution.setDvrPhone(normalizedPhone);
        execution.setAlertCode(request.getAlertCode());
        execution.setSubalertCode(request.getSubalertCode());
        execution.setChannel(request.getChannel());
        execution.setDurationSeconds(request.getDurationSeconds());
        execution.setRequestedBy(requestedBy);
        execution.setStatus("PENDING");
        execution = dvrAlertExecutionRepository.save(execution);

        try {
            JsonNode response = dvrCommandClient.execute(normalizedPhone, request);
            execution.setStatus(response.path("status").asText("ACK"));
            execution.setResponsePayload(response.toString());
            execution.setErrorMessage(null);
        } catch (RuntimeException ex) {
            execution.setStatus("ERROR");
            execution.setErrorMessage(ex.getMessage());
            execution.setResponsePayload(null);
        }

        execution = dvrAlertExecutionRepository.save(execution);
        return mapExecution(execution);
    }

    @Override
    public List<DvrAlertExecutionDTO> getExecutions(Long vehicleId, Integer limit) {
        getVehicle(vehicleId);
        int effectiveLimit = (limit == null || limit <= 0) ? 20 : limit;
        List<DvrAlertExecutionModel> data = dvrAlertExecutionRepository.findByVehicleModelIdOrderByCreatedAtDesc(vehicleId);
        return data.stream()
                .limit(effectiveLimit)
                .map(this::mapExecution)
                .toList();
    }

    private VehicleModel getVehicle(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle with id " + vehicleId + " not found"));
    }

    private List<DvrAlertDTO> mapAlerts(JsonNode alertsNode) {
        List<DvrAlertDTO> alerts = new ArrayList<>();
        if (alertsNode == null || !alertsNode.isArray()) {
            return alerts;
        }

        for (JsonNode item : alertsNode) {
            DvrAlertDTO alert = new DvrAlertDTO();
            alert.setCode(nullIfMissing(item, "code"));
            alert.setName(nullIfMissing(item, "name"));
            alert.setDescription(nullIfMissing(item, "description"));
            alert.setDurationSecondsDefault(item.hasNonNull("durationSecondsDefault") ? item.get("durationSecondsDefault").asInt() : null);
            alert.setRequiresChannel(item.path("requiresChannel").asBoolean(false));
            alert.setAvailable(item.path("available").asBoolean(false));

            List<DvrAlertSubalertDTO> subalerts = new ArrayList<>();
            JsonNode subalertsNode = item.path("subalerts");
            if (subalertsNode.isArray()) {
                for (JsonNode sub : subalertsNode) {
                    DvrAlertSubalertDTO subalert = new DvrAlertSubalertDTO();
                    subalert.setCode(nullIfMissing(sub, "code"));
                    subalert.setName(nullIfMissing(sub, "name"));
                    subalert.setDescription(nullIfMissing(sub, "description"));
                    subalert.setAvailable(sub.path("available").asBoolean(false));
                    subalerts.add(subalert);
                }
            }
            alert.setSubalerts(subalerts);
            alerts.add(alert);
        }

        return alerts;
    }

    private DvrAlertExecutionDTO mapExecution(DvrAlertExecutionModel model) {
        DvrAlertExecutionDTO dto = new DvrAlertExecutionDTO();
        dto.setId(model.getId());
        dto.setVehicleId(model.getVehicleModel().getId());
        dto.setLicensePlate(model.getVehicleModel().getLicensePlate());
        dto.setDvrPhone(model.getDvrPhone());
        dto.setAlertCode(model.getAlertCode());
        dto.setSubalertCode(model.getSubalertCode());
        dto.setChannel(model.getChannel());
        dto.setDurationSeconds(model.getDurationSeconds());
        dto.setStatus(model.getStatus());
        dto.setResponsePayload(model.getResponsePayload());
        dto.setErrorMessage(model.getErrorMessage());
        dto.setRequestedBy(model.getRequestedBy());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setUpdatedAt(model.getUpdatedAt());
        return dto;
    }

    private String nullIfMissing(JsonNode node, String field) {
        return node.hasNonNull(field) ? node.get(field).asText() : null;
    }
}
