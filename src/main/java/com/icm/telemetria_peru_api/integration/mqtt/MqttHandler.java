package com.icm.telemetria_peru_api.integration.mqtt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icm.telemetria_peru_api.dto.VehiclePayloadMqttDTO;
import com.icm.telemetria_peru_api.enums.FuelEfficiencyStatus;
import com.icm.telemetria_peru_api.models.*;
import com.icm.telemetria_peru_api.repositories.*;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MqttHandler {
    private final IMqttClient mqttClient;
    private final VehicleRepository vehicleRepository;
    private final VehicleIgnitionRepository vehicleIgnitionRepository;
    private final SpeedExcessLoggerRepository speedExcessLoggerRepository;
    private final FuelEfficiencyRepository fuelEfficiencyRepository;
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
            VehiclePayloadMqttDTO data = validateJson(jsonNode);
            //System.out.println("Processing JSON payload");


            if (data.getVehicleId() == null && data.getImei() != null) {
                Optional<VehicleModel> vehicleOptional = vehicleRepository.findByImei(data.getImei());
                data.setCompanyId(vehicleOptional.map(VehicleModel::getCompanyModel).map(CompanyModel::getId).orElse(null));
                data.setVehicleId(vehicleOptional.map(VehicleModel::getId).orElse(null));
                data.setLicensePlate(vehicleOptional.map(VehicleModel::getLicensePlate).orElse(null));

                if (vehicleOptional.isPresent()) {
                    analyzeTimestamp(data.getTimestamp(), data.getFuelInfo(), vehicleOptional.get());
                    handleAlarmInfo(vehicleOptional.get(), data.getAlarmInfo());
                    handleIgnitionInfo(vehicleOptional.get(), data.getIgnitionInfo());
                    fuelEfficiencyInfoLogs(vehicleOptional.get(), data);
                }
            }

            publisherData(jsonNode, data);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error processing the JSON: " + e.getMessage());
        }
    }

    private void publisherData(JsonNode jsonNode, VehiclePayloadMqttDTO vehiclePayloadMqttDTO) {
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

    private void fuelEfficiencyInfoLogs(VehicleModel vehicleModel, VehiclePayloadMqttDTO jsonNode) {
        FuelEfficiencyStatus determinate = determinateStatus(jsonNode.getIgnitionInfo(), jsonNode.getSpeed());

        FuelEfficiencyModel lastRecord = fuelEfficiencyRepository.findTopByVehicleModelIdOrderByCreatedAtDesc(vehicleModel.getId());

        if (lastRecord == null) {
            FuelEfficiencyModel newRecord = new FuelEfficiencyModel();
            newRecord.setFuelEfficiencyStatus(determinate);
            newRecord.setVehicleModel(vehicleModel);
            newRecord.setInitialFuel(jsonNode.getFuelInfo());
            fuelEfficiencyRepository.save(newRecord);
        }

        if (lastRecord != null && lastRecord.getFuelEfficiencyStatus() != determinate) {
            //Cierra el registro anterior
            lastRecord.setEndTime(ZonedDateTime.now());
            lastRecord.setFinalFuel(jsonNode.getFuelInfo());
            fuelEfficiencyRepository.save(lastRecord);

            //Crear e nuevo registro
            FuelEfficiencyModel newRecord = new FuelEfficiencyModel();
            newRecord.setFuelEfficiencyStatus(determinate);
            newRecord.setVehicleModel(vehicleModel);
            newRecord.setInitialFuel(jsonNode.getFuelInfo());
            fuelEfficiencyRepository.save(newRecord);
        }

        // Agregar un nuevo registro de velocidad
        if (lastRecord != null && lastRecord.getFuelEfficiencyStatus() == determinate) {
            if (jsonNode.getSpeed() != null && jsonNode.getSpeed() >= 1.0) {
                if (lastRecord.getSpeeds() == null) {
                    lastRecord.setSpeeds(new ArrayList<>());
                }
                lastRecord.getSpeeds().add(jsonNode.getSpeed());
                fuelEfficiencyRepository.save(lastRecord);
            }
        }
    }

    // Evento de cambio
    // Si se acumulan 3 eventos de cambios en el mismo vehiculo registar el cambio.


    private FuelEfficiencyStatus determinateStatus(Boolean ignitionInfo, Double speed) {
        if (ignitionInfo == null && speed == 0) {
            return FuelEfficiencyStatus.ESTACIONADO;
        }

        if (ignitionInfo != null && ignitionInfo) {
            if (speed == 0) {
                return FuelEfficiencyStatus.RALENTI;
            } else if (speed > 0) {
                return FuelEfficiencyStatus.OPERACION;
            }
        }
        return FuelEfficiencyStatus.ESTACIONADO;
    }
}
