package com.icm.telemetria_peru_api.integration.mqtt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.models.*;
import com.icm.telemetria_peru_api.repositories.*;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MqttHandler {
    private final IMqttClient mqttClient;
    private final VehicleRepository vehicleRepository;
    private final VehicleIgnitionRepository vehicleIgnitionRepository;
    private final SpeedExcessLoggerRepository speedExcessLoggerRepository;
    private final FuelRecordRepository fuelRecordRepository;
    private final AlarmRecordRepository alarmRecordRepository;
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
            //System.out.println("Processing JSON payload");
            Long companyId = null;
            Long vehicleId = jsonNode.has("vehicleId") ? jsonNode.get("vehicleId").asLong() : null;
            String licensePlate = jsonNode.has("licensePlate") ? jsonNode.get("licensePlate").asText() : null;
            String imei = jsonNode.has("imei") ? jsonNode.get("imei").asText() : null;
            Integer speed = jsonNode.has("speed") ? jsonNode.get("speed").asInt() : 0;
            String timestamp = jsonNode.has("timestamp") ? jsonNode.get("timestamp").asText() : null;
            Double fuelInfo = jsonNode.has("fuelInfo") ? jsonNode.get("fuelInfo").asDouble() : 0;
            Integer alarmInfo = jsonNode.has("alarmInfo") ? jsonNode.get("alarmInfo").asInt() : 0;
            Boolean ignitionInfo = jsonNode.has("ignitionInfo") ? jsonNode.get("ignitionInfo").asBoolean() : null;

            if (vehicleId == null && imei != null) {
                Optional<VehicleModel> vehicleOptional = vehicleRepository.findByImei(imei);
                vehicleId = vehicleOptional.map(VehicleModel::getId).orElse(null);
                licensePlate = vehicleOptional.map(VehicleModel::getLicensePlate).orElse(null);
                companyId = vehicleOptional.map(VehicleModel::getCompanyModel).map(CompanyModel::getId).orElse(null);

                if (vehicleOptional.isPresent()) {
                    analyzeTimestamp(timestamp, fuelInfo, vehicleOptional.get());
                    handleAlarmInfo(vehicleOptional.get(), alarmInfo);
                    handleIgnitionInfo(vehicleOptional.get(), ignitionInfo);
                    fuelEfficiencyInfoLogs(vehicleOptional.get(), timestamp);
                }
            }

            if (vehicleId != null) {
                mqttMessagePublisher.telData(vehicleId, jsonNode);
                mqttMessagePublisher.mapData(vehicleId, companyId, licensePlate, jsonNode);
                //SpeedExcessLogger(vehicleId, speed);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error processing the JSON: " + e.getMessage());
        }
    }

    private VehiclePayloadMqttDTO  validateJson(JsonNode jsonNode){
        Long vehicleId = jsonNode.has("vehicleId") ? jsonNode.get("vehicleId").asLong() : null;
        String licensePlate = jsonNode.has("licensePlate") ? jsonNode.get("licensePlate").asText() : null;
        String imei = jsonNode.has("imei") ? jsonNode.get("imei").asText() : null;
        Integer speed = jsonNode.has("speed") ? jsonNode.get("speed").asInt() : 0;
        String timestamp = jsonNode.has("timestamp") ? jsonNode.get("timestamp").asText() : null;
        Double fuelInfo = jsonNode.has("fuelInfo") ? jsonNode.get("fuelInfo").asDouble() : 0;
        Integer alarmInfo = jsonNode.has("alarmInfo") ? jsonNode.get("alarmInfo").asInt() : 0;
        Boolean ignitionInfo = jsonNode.has("ignitionInfo") ? jsonNode.get("ignitionInfo").asBoolean() : null;

        return new VehiclePayloadMqttDTO(vehicleId, licensePlate, imei, speed, timestamp, fuelInfo, alarmInfo, ignitionInfo);
    }

    /**
     * Logs an event if the vehicle's speed exceeds its maximum allowed limit.
     *
     * @param vehicleId the ID of the vehicle
     * @param speed     the current speed of the vehicle
     */
    private void SpeedExcessLogger(Long vehicleId, Integer speed) {
        Optional<VehicleModel> vehicle = vehicleRepository.findById(vehicleId);
        if (vehicle.isPresent()) {
            if (vehicle.get().getMaxSpeed() < speed) {
                SpeedExcessLoggerModel speedExcessLoggerModel = new SpeedExcessLoggerModel();
                speedExcessLoggerModel.setDescription("Maximum speed exceeded at " + speed + " km/h");
                speedExcessLoggerModel.setVehicleModel(vehicle.get());
                speedExcessLoggerRepository.save(speedExcessLoggerModel);
            }
        }
    }

    /**
     * Analyzes the received timestamp to determine if it falls within a specific interval
     * (the first 2 minutes of every tenth of the hour) and, if so, records a fuel data
     * entry associated with the vehicle.
     *
     * @param timestamp    the timestamp in Unix format (as a String) received in the message
     * @param fuelInfo     the fuel value associated with the vehicle
     * @param vehicleModel the vehicle model to which the data belongs
     */
    private void analyzeTimestamp(String timestamp, Double fuelInfo, VehicleModel vehicleModel) {
        try {

            if (fuelInfo == null && timestamp == null) {
                return;
            }

            long unixTimestamp = Long.parseLong(timestamp);

            LocalTime time = Instant.ofEpochSecond(unixTimestamp).atZone(ZoneId.systemDefault()).toLocalTime();

            int minute = time.getMinute();
            if (minute % 10 >= 0 && minute % 10 <= 2) {
                //System.out.println("Initial hour detected: " + time);
                FuelRecordModel fuelRecordModel = new FuelRecordModel();
                fuelRecordModel.setValueData(fuelInfo);
                fuelRecordModel.setVehicleModel(vehicleModel);
                fuelRecordRepository.save(fuelRecordModel);
            }
        } catch (Exception e) {
            System.out.println("Error analyzing the timestamp: " + e.getMessage());
        }
    }

    /**
     * Handles alarm information by checking if an alarm event exists
     * and saving a record associated with the vehicle if valid.
     *
     * @param vehicleModel the vehicle model associated with the alarm
     * @param alarmInfo    the alarm information value; a non-null and non-zero value indicates an active alarm
     */
    private void handleAlarmInfo(VehicleModel vehicleModel, Integer alarmInfo) {
        if (alarmInfo == null || alarmInfo == 0) {
            return;
        }
        AlarmRecordModel alarmRecordModel = new AlarmRecordModel();
        alarmRecordModel.setVehicleModel(vehicleModel);
        alarmRecordRepository.save(alarmRecordModel);
    }

    /**
     * Handles ignition status updates by checking the current ignition state
     * and comparing it with the last recorded state. If the state has changed
     * or there are no previous records, a new ignition record is saved.
     *
     * @param vehicleModel  the vehicle model associated with the ignition status
     * @param currentStatus the current ignition status (true for on, false for off)
     */
    private void handleIgnitionInfo(VehicleModel vehicleModel, Boolean currentStatus) {
        if (currentStatus == null) {
            return;
        }

        VehicleIgnitionModel lastRecord = vehicleIgnitionRepository.findTopByVehicleModelOrderByCreatedAtDesc(vehicleModel);

        if (lastRecord == null || !lastRecord.getStatus().equals(currentStatus)) {
            VehicleIgnitionModel newRecord = new VehicleIgnitionModel();
            newRecord.setVehicleModel(vehicleModel);
            newRecord.setStatus(currentStatus);
            vehicleIgnitionRepository.save(newRecord);
        }
    }

    private void fuelEfficiencyInfoLogs(VehicleModel vehicleModel, String time) {

    }
}
