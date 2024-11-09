package com.icm.telemetria_peru_api.integration.mqtt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icm.telemetria_peru_api.models.ImpactIncidentLoggingModel;
import com.icm.telemetria_peru_api.models.SpeedExcessLoggerModel;
import com.icm.telemetria_peru_api.models.VehicleModel;
import com.icm.telemetria_peru_api.repositories.SpeedExcessLoggerRepository;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Optional;

/**
 * Component responsible for subscribing to MQTT topics and processing received messages.
 * Utilizes the Paho MQTT client library for MQTT communication.
 */
@Component
public class MqttSubscriber {
    @Autowired
    private IMqttClient mqttClient;

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private SpeedExcessLoggerRepository speedExcessLoggerRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        String[] topics = {"data", "status", "prueba"};
        subscribeToTopics(topics);
        subscribeToJson("dataJ");
    }
    public void subscribeToJson(String topic) {
        try {
            mqttClient.subscribe(topic, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    //System.out.println("Mensaje MQTT recibido en el tema " + topic + ": " + payload);
                    processJsonPayload(payload);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void processJsonPayload(String payload) {
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);

            Long vehicleId = jsonNode.has("vehicleId") ? jsonNode.get("vehicleId").asLong() : null;
            String imei = jsonNode.has("imei") ? jsonNode.get("imei").asText() : null;
            Integer speed = jsonNode.has("speed") ? jsonNode.get("speed").asInt() : 0;


            if (vehicleId == null && imei != null) {
                Optional<VehicleModel> vehicleOptional = vehicleRepository.findByImei(imei);
                vehicleId = vehicleOptional.map(VehicleModel::getId).orElse(null);
            }

            if (vehicleId != null) {
                telData(vehicleId, jsonNode);
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

    private void telData(Long vehicleId, JsonNode originalJson) {
        try {
            // Agregar el ID del vehículo al JSON original
            ((ObjectNode) originalJson).put("vehicleId", vehicleId);

            // Serializar el JSON modificado a una cadena de texto
            String updatedPayload = objectMapper.writeValueAsString(originalJson);

            // Crear el mensaje MQTT
            MqttMessage mqttMessage = new MqttMessage(updatedPayload.getBytes());
            mqttMessage.setQos(1);

            // Publicar el mensaje en el tema telData/{vehicleId}
            String topic = "telData/" + vehicleId;
            mqttClient.publish(topic, mqttMessage);

            System.out.println("Mensaje enviado al tema " + topic + ": " + updatedPayload);

        } catch (MqttException | IOException e) {
            e.printStackTrace();
            System.out.println("Error al enviar el mensaje telData: " + e.getMessage());
        }
    }


    /* */

    public void subscribeToTopic(String topic) {
        try {
            mqttClient.subscribe(topic, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    //System.out.println("Mensaje MQTT recibido en el tema " + topic + ": " + payload);

                    // Procesa el payload aquí sin intentar deserializar
                    //System.out.println("Payload: " + payload);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribeToTopics(String[] topics) {
        try {
            for (String topic : topics) {
                mqttClient.subscribe(topic, new IMqttMessageListener() {
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        String payload = new String(message.getPayload());
                       // System.out.println("Mensaje MQTT recibido en el tema " + topic + ": " + payload);

                        // Procesa el payload aquí sin intentar deserializar
                        //System.out.println("Payload: " + payload);
                    }
                });
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
