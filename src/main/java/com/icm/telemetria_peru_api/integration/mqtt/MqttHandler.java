package com.icm.telemetria_peru_api.integration.mqtt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icm.telemetria_peru_api.models.*;
import com.icm.telemetria_peru_api.repositories.AlarmRecordRepository;
import com.icm.telemetria_peru_api.repositories.FuelRecordRepository;
import com.icm.telemetria_peru_api.repositories.SpeedExcessLoggerRepository;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class MqttHandler {
    @Autowired
    private IMqttClient mqttClient;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private SpeedExcessLoggerRepository speedExcessLoggerRepository;

    @Autowired
    private FuelRecordRepository fuelRecordRepository;

    @Autowired
    private AlarmRecordRepository alarmRecordRepository;

    @Autowired
    private MqttMessagePublisher mqttMessagePublisher;

    private ObjectMapper objectMapper = new ObjectMapper();


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



            if (vehicleId == null && imei != null) {
                Optional<VehicleModel> vehicleOptional = vehicleRepository.findByImei(imei);
                vehicleId = vehicleOptional.map(VehicleModel::getId).orElse(null);
                licensePlate = vehicleOptional.map(VehicleModel::getLicensePlate).orElse(null);
                companyId = vehicleOptional
                        .map(VehicleModel::getCompanyModel)
                        .map(CompanyModel::getId)
                        .orElse(null);

                if (vehicleOptional.isPresent() && fuelInfo != null && timestamp != null) {
                    analyzeTimestamp(timestamp, fuelInfo, vehicleOptional.get());
                    handleAlarmInfo(vehicleOptional.get());
                }
            }

            if (vehicleId != null) {
                mqttMessagePublisher.telData(vehicleId, jsonNode);
                mqttMessagePublisher.mapData(vehicleId, companyId, licensePlate, jsonNode);
                //SpeedExcessLogger(vehicleId, speed);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al procesar el JSON: " + e.getMessage());
        }
    }

    private void SpeedExcessLogger(Long vehicleId, Integer speed){
        Optional<VehicleModel> vehicle = vehicleRepository.findById(vehicleId);
        if (vehicle.isPresent()){
            if (vehicle.get().getMaxSpeed() < speed){
                SpeedExcessLoggerModel speedExcessLoggerModel = new SpeedExcessLoggerModel();
                speedExcessLoggerModel.setDescription("Velocidad maxima exedida en " + speed + " km/h");
                speedExcessLoggerModel.setVehicleModel(vehicle.get());
                speedExcessLoggerRepository.save(speedExcessLoggerModel);
            }
        }
    }


    private void analyzeTimestamp(String timestamp, Double fuelInfo, VehicleModel vehicleModel ) {
        try {
            // Convertir el timestamp de String a long
            long unixTimestamp = Long.parseLong(timestamp);

            // Convertir el Unix timestamp a hora local
            LocalTime time = Instant.ofEpochSecond(unixTimestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime();

            // Verificar si está en los primeros 2 minutos de cualquier hora
            int minute = time.getMinute();
            if (minute % 10 >= 0 && minute % 10 <= 2) {
                //System.out.println("Hora inicial detectada: " + time);
                FuelRecordModel fuelRecordModel = new FuelRecordModel();
                fuelRecordModel.setValueData(fuelInfo);
                fuelRecordModel.setVehicleModel(vehicleModel);
                fuelRecordRepository.save(fuelRecordModel);
            }
        } catch (Exception e) {
            System.out.println("Error al analizar el timestamp: " + e.getMessage());
        }
    }

    private void handleAlarmInfo(VehicleModel vehicleModel) {
        AlarmRecordModel alarmRecordModel = new AlarmRecordModel();
        alarmRecordModel.setVehicleModel(vehicleModel);
        alarmRecordRepository.save(alarmRecordModel);
    }
}
