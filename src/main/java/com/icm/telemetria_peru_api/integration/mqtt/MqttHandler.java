package com.icm.telemetria_peru_api.integration.mqtt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.integration.mqtt.handlers.*;
import com.icm.telemetria_peru_api.models.CompanyModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.*;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.IMqttClient;
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
    private final SpeedExcessHandler speedExcessHandler;
    private final IMqttClient mqttClient;
    private final VehicleRepository vehicleRepository;

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
            System.out.println("Processing JSON payload");

            System.out.println("Entrp");
            if (data.getVehicleId() == null && data.getImei() != null) {
                Optional<VehicleModel> vehicleOptional = vehicleRepository.findByImei(data.getImei());
                data.setCompanyId(vehicleOptional.map(VehicleModel::getCompanyModel).map(CompanyModel::getId).orElse(null));
                data.setVehicleId(vehicleOptional.map(VehicleModel::getId).orElse(null));
                data.setLicensePlate(vehicleOptional.map(VehicleModel::getLicensePlate).orElse(null));
                System.out.println("Entrp 2");
                if (vehicleOptional.isPresent()) {
                    System.out.println("Entrp 3");
                    fuelRecordHandler.analyzeFuelTimestamp(data, vehicleOptional.get());
                    alarmHandler.saveAlarmRecord(vehicleOptional.get(), data.getAlarmInfo());
                    ignitionHandler.updateIgnitionStatus(vehicleOptional.get(), data.getIgnitionInfo());
                    fuelEfficiencyHandler.processFuelEfficiencyInfo(vehicleOptional.get(), data);
                    System.out.println("Entrp 4");
                    //speedExcessHandler.logSpeedExcess(vehicleOptional.get().getId(), data.getSpeed());
                }
                publisherData(data, jsonNode);

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error processing the JSON: " + e.getMessage());
        }
    }

    private void publisherData( VehiclePayloadMqttDTO vehiclePayloadMqttDTO, JsonNode jsonNode) {
        if (vehiclePayloadMqttDTO.getVehicleId() != null) {
            mqttMessagePublisher.telData(vehiclePayloadMqttDTO.getVehicleId(), jsonNode);
            mqttMessagePublisher.mapData(vehiclePayloadMqttDTO.getVehicleId(), vehiclePayloadMqttDTO.getCompanyId(), vehiclePayloadMqttDTO.getLicensePlate(), jsonNode);
            //SpeedExcessLogger(vehicleId, speed);
        }
    }

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

        return new VehiclePayloadMqttDTO(vehicleId, companyId, licensePlate, imei, speed, timestamp, fuelInfo, alarmInfo, ignitionInfo);
    }
}
