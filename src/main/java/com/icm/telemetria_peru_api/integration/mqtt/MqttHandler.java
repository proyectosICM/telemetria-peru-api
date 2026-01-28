package com.icm.telemetria_peru_api.integration.mqtt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.dto.VehicleSnapshotDTO;
import com.icm.telemetria_peru_api.integration.mqtt.handlers.*;
import com.icm.telemetria_peru_api.models.CompanyModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MqttHandler {

    private final IgnitionHandler ignitionHandler;
    private final AlarmHandler alarmHandler;
    private final FuelRecordHandler fuelRecordHandler;
    private final FuelEfficiencyDailyHandler fuelEfficiencyDailyHandler;
    private final GasChangeHandler gasChangeHandler;
    private final GasRecordHandler gasRecordHandler;
    private final VehicleRepository vehicleRepository;
    private final SpeedExcessHandler speedExcessHandler;
    private final FuelReportHandler fuelReportHandler;
    private final VehicleSnapshotHandler vehicleSnapshotHandler;

    private final MqttMessagePublisher mqttMessagePublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void processJsonPayload(String payload) {
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);

            VehiclePayloadMqttDTO data = validateJson(jsonNode);
            VehicleSnapshotDTO snapshotDTO = createVehicleSnapshot(jsonNode);

            if (data.getVehicleId() == null && data.getImei() != null) {
                Optional<VehicleModel> vehicleOptional = vehicleRepository.findByImei(data.getImei());

                data.setCompanyId(vehicleOptional.map(VehicleModel::getCompanyModel).map(CompanyModel::getId).orElse(null));
                data.setVehicleId(vehicleOptional.map(VehicleModel::getId).orElse(null));
                data.setLicensePlate(vehicleOptional.map(VehicleModel::getLicensePlate).orElse(null));

                if (vehicleOptional.isPresent()) {
                    VehicleModel vehicle = vehicleOptional.get();

                    publishDataWithErrorHandling(data, jsonNode);
                    processHandlersWithErrorHandling(snapshotDTO, data, vehicle);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error processing the JSON: " + e.getMessage());
        }
    }

    private VehiclePayloadMqttDTO validateJson(JsonNode jsonNode) {
        VehiclePayloadMqttDTO dto = new VehiclePayloadMqttDTO();

        dto.setVehicleId(jsonNode.has("vehicleId") ? jsonNode.get("vehicleId").asLong() : null);
        dto.setCompanyId(jsonNode.has("companyId") ? jsonNode.get("companyId").asLong() : null);
        dto.setLicensePlate(jsonNode.has("licensePlate") ? jsonNode.get("licensePlate").asText() : null);
        dto.setImei(jsonNode.has("imei") ? jsonNode.get("imei").asText() : null);

        dto.setSpeed(jsonNode.has("speed") ? jsonNode.get("speed").asDouble() : 0.0);
        dto.setTimestamp(jsonNode.has("timestamp") ? jsonNode.get("timestamp").asText() : null);

        dto.setFuelInfo(jsonNode.has("fuelInfo") ? jsonNode.get("fuelInfo").asDouble() : 0.0);
        dto.setAlarmInfo(jsonNode.has("alarmInfo") ? jsonNode.get("alarmInfo").asInt() : 0);

        // Soporta boolean o 0/1 (por si acaso)
        dto.setIgnitionInfo(parseBoolean0_1(jsonNode, "ignitionInfo"));

        Double latitude = jsonNode.has("latitude") ? jsonNode.get("latitude").asDouble() : null;
        Double longitude = jsonNode.has("longitude") ? jsonNode.get("longitude").asDouble() : null;
        dto.setCoordinates((latitude != null && longitude != null) ? latitude + "," + longitude : null);

        dto.setGasInfo(jsonNode.has("gasInfo") ? jsonNode.get("gasInfo").asDouble() : null);

        // ===== NUEVOS CAMPOS =====
        dto.setMovement(jsonNode.has("movement") ? jsonNode.get("movement").asInt() : null);
        dto.setInstantMovement(jsonNode.has("instantMovement") ? jsonNode.get("instantMovement").asInt() : null);
        dto.setVehicleSpeedIo(jsonNode.has("vehicleSpeedIo") ? jsonNode.get("vehicleSpeedIo").asInt() : null);
        dto.setExternalVoltage(jsonNode.has("externalVoltage") ? jsonNode.get("externalVoltage").asInt() : null);

        return dto;
    }

    private Boolean parseBoolean0_1(JsonNode jsonNode, String field) {
        if (!jsonNode.has(field) || jsonNode.get(field).isNull()) return null;
        JsonNode n = jsonNode.get(field);
        if (n.isBoolean()) return n.asBoolean();
        if (n.isNumber()) return n.asInt() == 1;
        // "0"/"1"/"true"/"false"
        String s = n.asText();
        if ("1".equals(s)) return true;
        if ("0".equals(s)) return false;
        return Boolean.parseBoolean(s);
    }

    private VehicleSnapshotDTO createVehicleSnapshot(JsonNode jsonNode) {
        Long vehicleId = jsonNode.has("vehicleId") ? jsonNode.get("vehicleId").asLong() : null;
        Long companyId = jsonNode.has("companyId") ? jsonNode.get("companyId").asLong() : null;
        String licensePlate = jsonNode.has("licensePlate") ? jsonNode.get("licensePlate").asText() : null;
        String imei = jsonNode.has("imei") ? jsonNode.get("imei").asText() : null;
        String timestamp = jsonNode.has("timestamp") ? jsonNode.get("timestamp").asText() : null;
        Double fuelInfo = jsonNode.has("fuelInfo") ? jsonNode.get("fuelInfo").asDouble() : 0;
        Boolean alarmInfo = jsonNode.has("alarmInfo") ? jsonNode.get("alarmInfo").asBoolean() : null;
        Boolean ignitionInfo = parseBoolean0_1(jsonNode, "ignitionInfo");
        String latitude = jsonNode.has("latitude") ? jsonNode.get("latitude").asText() : null;
        String longitude = jsonNode.has("longitude") ? jsonNode.get("longitude").asText() : null;
        Double gasInfo = jsonNode.has("gasInfo") ? jsonNode.get("gasInfo").asDouble() : null;
        Integer snapshotSpeed = jsonNode.has("speed") ? jsonNode.get("speed").asInt() : null;

        return new VehicleSnapshotDTO(
                null,
                vehicleId,
                companyId,
                licensePlate,
                imei,
                timestamp,
                fuelInfo,
                alarmInfo,
                ignitionInfo,
                (latitude != null && longitude != null) ? (latitude + "," + longitude) : null,
                gasInfo,
                snapshotSpeed,
                latitude,
                longitude
        );
    }

    private void processHandlersWithErrorHandling(VehicleSnapshotDTO snapshotDTO, VehiclePayloadMqttDTO data, VehicleModel vehicle) {
        executeSafely(() -> fuelReportHandler.saveFuelReport(data, vehicle), "fuelReportHandler.saveFuelReport");
        executeSafely(() -> fuelRecordHandler.analyzeFuelTimestamp(data, vehicle), "fuelRecordHandler.analyzeFuelTimestamp");
        executeSafely(() -> gasChangeHandler.saveGasChangeRecord(data, vehicle), "gasChangeHandler.saveGasChangeRecord");
        executeSafely(() -> gasRecordHandler.saveGasRecordModel(data, vehicle), "gasRecordHandler.saveGasRecordModel");
        executeSafely(() -> alarmHandler.saveAlarmRecord(vehicle, data.getAlarmInfo()), "alarmHandler.saveAlarmRecord");
        executeSafely(() -> ignitionHandler.updateIgnitionStatus(vehicle, data.getIgnitionInfo()), "ignitionHandler.updateIgnitionStatus");

        // IMPORTANTE: usa process(...) (nuevo)
        executeSafely(() -> fuelEfficiencyDailyHandler.process(vehicle, data), "fuelEfficiencyDailyHandler.process");

        executeSafely(() -> speedExcessHandler.logSpeedExcess(vehicle, data), "speedExcessHandler.logSpeedExcess");
        executeSafely(() -> vehicleSnapshotHandler.saveVehicleSnapshot(snapshotDTO, vehicle), "vehicleSnapshotHandler.saveVehicleSnapshot");
    }

    private void publishDataWithErrorHandling(VehiclePayloadMqttDTO data, JsonNode jsonNode) {
        executeSafely(() -> publisherData(data, jsonNode), "publisherData");
    }

    private void executeSafely(Runnable action, String actionName) {
        try {
            action.run();
        } catch (Exception e) {
            System.err.println("Error in " + actionName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void publisherData(VehiclePayloadMqttDTO vehiclePayloadMqttDTO, JsonNode jsonNode) {
        if (vehiclePayloadMqttDTO.getVehicleId() != null) {
            mqttMessagePublisher.telData(vehiclePayloadMqttDTO.getVehicleId(), jsonNode);
            mqttMessagePublisher.mapData(
                    vehiclePayloadMqttDTO.getVehicleId(),
                    vehiclePayloadMqttDTO.getCompanyId(),
                    vehiclePayloadMqttDTO.getLicensePlate(),
                    jsonNode
            );
        }
    }
}
