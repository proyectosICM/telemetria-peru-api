package com.icm.telemetria_peru_api.integration.mqtt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;

public class MqttMessagePublisher {
    private IMqttClient mqttClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    public MqttMessagePublisher(IMqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    public void telData(Long vehicleId, JsonNode originalJson) {
        try {
            // Agregar el ID del vehículo al JSON original
            ((ObjectNode) originalJson).put("vehicleId", vehicleId);

            // Serializar el JSON modificado a una cadena de texto
            String updatedPayload = objectMapper.writeValueAsString(originalJson);

            // Crear el mensaje MQTT
            MqttMessage mqttMessage = new MqttMessage(updatedPayload.getBytes());
            mqttMessage.setQos(1);
            mqttMessage.setRetained(true);

            // Publicar el mensaje en el tema telData/{vehicleId}
            String topic = "telData/" + vehicleId;
            mqttClient.publish(topic, mqttMessage);

            //System.out.println("Mensaje enviado al tema " + topic + ": " + updatedPayload);
        } catch (MqttException | IOException e) {
            e.printStackTrace();
            System.out.println("Error al enviar el mensaje telData: " + e.getMessage());
        }
    }

    public void mapData(Long vehicleId, Long companyId, String licensePlate, JsonNode originalJson) {
        try {
            // Agregar el ID del vehículo y la placa al JSON original
            ((ObjectNode) originalJson).put("vehicleId", vehicleId);
            ((ObjectNode) originalJson).put("licensePlate", licensePlate);

            // Serializar el JSON modificado a una cadena de texto
            String updatedPayload = objectMapper.writeValueAsString(originalJson);

            // Crear el mensaje MQTT
            MqttMessage mqttMessage = new MqttMessage(updatedPayload.getBytes());
            mqttMessage.setQos(1);
            mqttMessage.setRetained(true);

            // Publicar el mensaje en el tema mapa/{vehicleId}
            String topic = "mapData/" + companyId;
            mqttClient.publish(topic, mqttMessage);

            //System.out.println("Mensaje enviado al tema " + topic + ": " + updatedPayload);

        } catch (MqttException | IOException e) {
            e.printStackTrace();
            System.out.println("Error al enviar el mensaje mapData: " + e.getMessage());
        }
    }
}