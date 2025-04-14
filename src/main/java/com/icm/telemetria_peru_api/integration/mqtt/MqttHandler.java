package com.icm.telemetria_peru_api.integration.mqtt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.integration.mqtt.handlers.*;
import com.icm.telemetria_peru_api.models.CompanyModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.*;
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
    private final FuelEfficiencyHandler fuelEfficiencyHandler;
    private final GasChangeHandler gasChangeHandler;
    private final GasRecordHandler gasRecordHandler;
    private final VehicleRepository vehicleRepository;
    private final SpeedExcessHandler speedExcessHandler;

    private final MqttMessagePublisher mqttMessagePublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Processes the JSON message received via MQTT, extracting relevant information
     * about vehicles and events. Depending on the content, it triggers related functions
     * such as ignition logging, alarms, speed excess, or fuel consumption, and publishes
     * processed messages to other systems.
     *
     * @param payload the JSON message received via MQTT
     */
    public void processJsonPayload(String payload) {
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            VehiclePayloadMqttDTO data = validateJson(jsonNode);
            //System.out.println("Processing JSON payload");

            if (data.getVehicleId() == null && data.getImei() != null) {
                Optional<VehicleModel> vehicleOptional = vehicleRepository.findByImei(data.getImei());
                data.setCompanyId(vehicleOptional.map(VehicleModel::getCompanyModel).map(CompanyModel::getId).orElse(null));
                data.setVehicleId(vehicleOptional.map(VehicleModel::getId).orElse(null));
                data.setLicensePlate(vehicleOptional.map(VehicleModel::getLicensePlate).orElse(null));
                if (vehicleOptional.isPresent()) {
                    VehicleModel vehicle = vehicleOptional.get();
                    publishDataWithErrorHandling(data, jsonNode);
                    processHandlersWithErrorHandling(data, vehicle);
                    //speedExcessHandler.logSpeedExcess(vehicleOptional.get().getId(), data.getSpeed());

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error processing the JSON: " + e.getMessage());
        }
    }

    /**
     * Validates and extracts relevant fields from a given JSON node, converting them into a
     * VehiclePayloadMqttDTO object. Fields are checked for existence before retrieval,
     * and default values are assigned if they are missing.
     *
     * @param jsonNode The input JSON node containing vehicle data.
     * @return A VehiclePayloadMqttDTO object populated with the extracted data.
     */
    private VehiclePayloadMqttDTO validateJson(JsonNode jsonNode) {
        Long vehicleId = jsonNode.has("vehicleId") ? jsonNode.get("vehicleId").asLong() : null;
        Long companyId = jsonNode.has("companyId") ? jsonNode.get("companyId").asLong() : null;
        String licensePlate = jsonNode.has("licensePlate") ? jsonNode.get("licensePlate").asText() : null;
        String imei = jsonNode.has("imei") ? jsonNode.get("imei").asText() : null;
        Double speed = jsonNode.has("speed") ? jsonNode.get("speed").asDouble() : 0;
        String timestamp = jsonNode.has("timestamp") ? jsonNode.get("timestamp").asText() : null;
        Double fuelInfo = jsonNode.has("fuelInfo") ? jsonNode.get("fuelInfo").asDouble() : 0;
        Integer alarmInfo = jsonNode.has("alarmInfo") ? jsonNode.get("alarmInfo").asInt() : 0;
        Boolean ignitionInfo = jsonNode.has("ignitionInfo") ? jsonNode.get("ignitionInfo").asBoolean() : null;
        Double latitude = jsonNode.has("latitude") ? jsonNode.get("latitude").asDouble() : null;
        Double longitude = jsonNode.has("longitude") ? jsonNode.get("longitude").asDouble() : null;
        Double gasInfo = jsonNode.has("gasInfo") ? jsonNode.get("gasInfo").asDouble() : null;

        String coordinates = (latitude != null && longitude != null) ? latitude + "," + longitude : null;

        return new VehiclePayloadMqttDTO(vehicleId, companyId, licensePlate, imei, speed, timestamp, fuelInfo, alarmInfo, ignitionInfo, coordinates, gasInfo);
    }

    private void processHandlersWithErrorHandling(VehiclePayloadMqttDTO data, VehicleModel vehicle) {
        //executeSafely(() -> );
        executeSafely(() -> fuelRecordHandler.analyzeFuelTimestamp(data, vehicle), "fuelRecordHandler.analyzeFuelTimestamp");
        executeSafely(() -> gasChangeHandler.saveGasChangeRecord(data, vehicle), "gasChangeHandler.analyzeFuelTimestamp");
        executeSafely(() -> gasRecordHandler.saveGasRecordModel(data, vehicle), "gasRecordHandler.analyzeFuelTimestamp");
        executeSafely(() -> alarmHandler.saveAlarmRecord(vehicle, data.getAlarmInfo()), "alarmHandler.saveAlarmRecord");
        executeSafely(() -> ignitionHandler.updateIgnitionStatus(vehicle, data.getIgnitionInfo()), "ignitionHandler.updateIgnitionStatus");
        executeSafely(() -> fuelEfficiencyHandler.processFuelEfficiencyInfo(vehicle, data), "fuelEfficiencyHandler.processFuelEfficiencyInfo");
        executeSafely(() -> speedExcessHandler.logSpeedExcess(vehicle, data), "speedExcessHandler.logSpeedExcess");
        // speedExcessHandler
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


    private void publisherData( VehiclePayloadMqttDTO vehiclePayloadMqttDTO, JsonNode jsonNode) {
        if (vehiclePayloadMqttDTO.getVehicleId() != null) {
            mqttMessagePublisher.telData(vehiclePayloadMqttDTO.getVehicleId(), jsonNode);
            mqttMessagePublisher.mapData(vehiclePayloadMqttDTO.getVehicleId(), vehiclePayloadMqttDTO.getCompanyId(), vehiclePayloadMqttDTO.getLicensePlate(), jsonNode);
            //SpeedExcessLogger(vehicleId, speed);
        }
    }


}
