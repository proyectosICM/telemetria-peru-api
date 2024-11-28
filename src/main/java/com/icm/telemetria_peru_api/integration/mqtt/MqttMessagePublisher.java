package com.icm.telemetria_peru_api.integration.mqtt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icm.telemetria_peru_api.models.FuelRecordModel;
import com.icm.telemetria_peru_api.repositories.FuelRecordRepository;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
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

    public void fuelEfficient(Long logId, Long vehicleId) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setQos(1);
            mqttMessage.setRetained(false); // Generalmente no es necesario que sea retenido para datos dinámicos
            mqttMessage.setPayload(logId.toString().getBytes()); // Convierte Long a String y luego a bytes

            // Publicar el mensaje en el tema fuelEfficient/{vehicleId}
            String topic = "fuelEfficient/" + vehicleId;

            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.publish(topic, mqttMessage);
                System.out.println("Mensaje enviado al tema " + topic);
            } else {
                System.out.println("MQTT Client no está conectado.");
            }
        } catch (MqttException e) {
            System.err.println("Error al enviar el mensaje fuelEfficient: " + e.getMessage());
            e.printStackTrace(); // Para debugging
        }
    }
}
