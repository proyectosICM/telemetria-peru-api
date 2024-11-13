package com.icm.telemetria_peru_api.integration.mqtt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icm.telemetria_peru_api.models.CompanyModel;
import com.icm.telemetria_peru_api.models.SpeedExcessLoggerModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.SpeedExcessLoggerRepository;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
    private MqttMessagePublisher mqttMessagePublisher;

    private ObjectMapper objectMapper = new ObjectMapper();


    public void processJsonPayload(String payload) {
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            System.out.println("Processing JSON payload");
            Long companyId = null;
            Long vehicleId = jsonNode.has("vehicleId") ? jsonNode.get("vehicleId").asLong() : null;
            String licensePlate = jsonNode.has("licensePlate") ? jsonNode.get("licensePlate").asText() : null;
            String imei = jsonNode.has("imei") ? jsonNode.get("imei").asText() : null;
            Integer speed = jsonNode.has("speed") ? jsonNode.get("speed").asInt() : 0;

            if (vehicleId == null && imei != null) {
                Optional<VehicleModel> vehicleOptional = vehicleRepository.findByImei(imei);
                vehicleId = vehicleOptional.map(VehicleModel::getId).orElse(null);
                licensePlate = vehicleOptional.map(VehicleModel::getLicensePlate).orElse(null);
                companyId = vehicleOptional
                        .map(VehicleModel::getCompanyModel)
                        .map(CompanyModel::getId)
                        .orElse(null);
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
}
