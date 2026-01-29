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

import java.util.Optional;
import java.util.concurrent.*;

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

    /**
     * Pool pequeño para no reventar DB.
     * Cola limitada para no acumular infinito si DB se pone lenta.
     * Si la cola se llena, DiscardPolicy descarta silenciosamente (puedes cambiar a CallerRunsPolicy).
     */
    private final ExecutorService feExecutor =
            new ThreadPoolExecutor(
                    2, 2,
                    0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(200),
                    new ThreadPoolExecutor.DiscardPolicy()
            );

    public void processJsonPayload(String payload) {
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);

            VehiclePayloadMqttDTO data = validateJson(jsonNode);
            VehicleSnapshotDTO snapshotDTO = createVehicleSnapshot(jsonNode);

            Optional<VehicleModel> vehicleOpt = Optional.empty();

            // 1) Si viene vehicleId, buscar por ID
            if (data.getVehicleId() != null) {
                vehicleOpt = vehicleRepository.findById(data.getVehicleId());
            }

            // 2) Si no viene vehicleId o no existe, buscar por IMEI
            if (vehicleOpt.isEmpty() && data.getImei() != null && !data.getImei().isBlank()) {
                vehicleOpt = vehicleRepository.findByImei(data.getImei());
            }

            // 3) Si no encontramos vehículo, no procesamos
            if (vehicleOpt.isEmpty()) {
                System.out.println("[MQTT] Vehicle not found. vehicleId=" + data.getVehicleId()
                        + " imei=" + data.getImei());
                return;
            }

            VehicleModel vehicle = vehicleOpt.get();

            // 4) Completar campos faltantes (solo si están null)
            if (data.getVehicleId() == null) data.setVehicleId(vehicle.getId());
            if (data.getCompanyId() == null) {
                data.setCompanyId(Optional.ofNullable(vehicle.getCompanyModel()).map(CompanyModel::getId).orElse(null));
            }
            if (data.getLicensePlate() == null) data.setLicensePlate(vehicle.getLicensePlate());

            // 5) Publicar y procesar siempre
            publishDataWithErrorHandling(data, jsonNode);
            processHandlersWithErrorHandling(snapshotDTO, data, vehicle);

        } catch (Exception e) {
            System.err.println("[MQTT] Error processing JSON: " + e.getMessage());
            e.printStackTrace();
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

        // alarmInfo normalmente viene 0/1 -> lo guardas como int en DTO
        dto.setAlarmInfo(jsonNode.has("alarmInfo") ? jsonNode.get("alarmInfo").asInt() : 0);

        // ignitionInfo: si viene 0/1, asBoolean() puede fallar => soporta ambos
        if (jsonNode.has("ignitionInfo")) {
            JsonNode ign = jsonNode.get("ignitionInfo");
            if (ign.isBoolean()) dto.setIgnitionInfo(ign.asBoolean());
            else dto.setIgnitionInfo(ign.asInt() == 1);
        } else {
            dto.setIgnitionInfo(null);
        }

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

    private VehicleSnapshotDTO createVehicleSnapshot(JsonNode jsonNode) {
        Long vehicleId = jsonNode.has("vehicleId") ? jsonNode.get("vehicleId").asLong() : null;
        Long companyId = jsonNode.has("companyId") ? jsonNode.get("companyId").asLong() : null;
        String licensePlate = jsonNode.has("licensePlate") ? jsonNode.get("licensePlate").asText() : null;
        String imei = jsonNode.has("imei") ? jsonNode.get("imei").asText() : null;
        String timestamp = jsonNode.has("timestamp") ? jsonNode.get("timestamp").asText() : null;
        Double fuelInfo = jsonNode.has("fuelInfo") ? jsonNode.get("fuelInfo").asDouble() : 0.0;

        // alarmInfo suele ser 0/1 => conviértelo a Boolean aquí también
        Boolean alarmInfo = null;
        if (jsonNode.has("alarmInfo")) {
            JsonNode a = jsonNode.get("alarmInfo");
            alarmInfo = a.isBoolean() ? a.asBoolean() : (a.asInt() == 1);
        }

        Boolean ignitionInfo = null;
        if (jsonNode.has("ignitionInfo")) {
            JsonNode ign = jsonNode.get("ignitionInfo");
            ignitionInfo = ign.isBoolean() ? ign.asBoolean() : (ign.asInt() == 1);
        }

        String latitude = jsonNode.has("latitude") ? jsonNode.get("latitude").asText() : null;
        String longitude = jsonNode.has("longitude") ? jsonNode.get("longitude").asText() : null;
        Double gasInfo = jsonNode.has("gasInfo") ? jsonNode.get("gasInfo").asDouble() : null;
        Integer snapshotSpeed = jsonNode.has("speed") ? jsonNode.get("speed").asInt() : null;

        String coords = (latitude != null && longitude != null) ? (latitude + "," + longitude) : null;

        // Ajusta el constructor al orden exacto de tu record/DTO
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
                coords,
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

        // Fuel Efficiency: async
        feExecutor.submit(() ->
                executeSafely(() -> fuelEfficiencyDailyHandler.process(vehicle.getId(), data), "fuelEfficiencyDailyHandler.process")
        );

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
            System.err.println("[MQTT] Error in " + actionName + ": " + e.getMessage());
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
