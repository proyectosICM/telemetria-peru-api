package com.icm.telemetria_peru_api.integration.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.icm.telemetria_peru_api.models.FuelRecordModel;
import com.icm.telemetria_peru_api.models.VehicleSnapshotModel;
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


    /**
     * Publishes a shutdown message to an MQTT topic for a specific vehicle.
     *
     * This method sends a shutdown notification to the MQTT topic associated with the given vehicle ID.
     * The message indicates that the checklist for the current day does not exist. The message is retained
     * and uses a QoS level of 1 to ensure delivery at least once.
     *
     * @param vehicleId The ID of the vehicle for which the shutdown message is being sent.
     */
    public void CheckListShutDown(Long vehicleId){
        try{
            byte[] payload = "Shutdown due to non-existent checklist of the day".getBytes();
            MqttMessage mqttMessage = new MqttMessage(payload);
            mqttMessage.setQos(1);
            mqttMessage.setRetained(true);
            String topic = "checklist/" + vehicleId;
            mqttClient.publish(topic, mqttMessage);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error sending CheckListShutDown message: " + e.getMessage());
        }
    }

    /**
     * Publishes an impact incident message to an MQTT topic for a specific vehicle.
     *
     * This method sends a notification to the MQTT topic associated with the given vehicle ID,
     * indicating that an impact incident has occurred. The message is retained and uses a QoS
     * level of 1 to ensure delivery at least once.
     *
     * @param vehicleId The ID of the vehicle for which the impact incident message is being sent.
     */
    public void ImpactIncident(Long vehicleId){
        try{
            byte[] payload = "Impact incident".getBytes();
            MqttMessage mqttMessage = new MqttMessage(payload);
            mqttMessage.setQos(1);
            mqttMessage.setRetained(true);
            String topic = "impact/" + vehicleId;
            mqttClient.publish(topic, mqttMessage);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error sending message ImpactIncident: " + e.getMessage());
        }
    }

    public void snapshot(Long vehicleId, VehicleSnapshotModel vehicleSnapshotModel) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String payload = objectMapper.writeValueAsString(vehicleSnapshotModel);

            MqttMessage mqttMessage = new MqttMessage(payload.getBytes());
            mqttMessage.setQos(1);
            mqttMessage.setRetained(true);

            String topic = "mapSnapshot/" + vehicleId;
            mqttClient.publish(topic, mqttMessage);

        } catch (MqttException | JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("Error sending snapshot message: " + e.getMessage());
        }
    }


    /**
     * Sends telemetry data for a specific vehicle to an MQTT topic.
     *
     * This method updates the provided JSON payload by adding the vehicleId field,
     * then serializes the JSON object and publishes it to the MQTT topic. The message
     * is retained and uses a QoS level of 1 for at-least-once delivery.
     *
     * @param vehicleId The ID of the vehicle for which telemetry data is being sent.
     * @param originalJson The original JSON data containing telemetry information.
     *
     * @throws MqttException If an error occurs during MQTT communication.
     * @throws IOException If an error occurs while processing the JSON data.
     */
    public void telData(Long vehicleId, JsonNode originalJson) {
        try {
            ((ObjectNode) originalJson).put("vehicleId", vehicleId);

            String updatedPayload = objectMapper.writeValueAsString(originalJson);

            MqttMessage mqttMessage = new MqttMessage(updatedPayload.getBytes());
            mqttMessage.setQos(1);
            mqttMessage.setRetained(true);

            String topic = "telData/" + vehicleId;
            mqttClient.publish(topic, mqttMessage);

            //System.out.println("Message sent to topic " + topic + ": " + updatedPayload);
        } catch (MqttException | IOException e) {
            e.printStackTrace();
            System.out.println("Error sending telData message: " + e.getMessage());
        }
    }

    /**
     * Sends mapping data for a specific vehicle and company to an MQTT topic.
     *
     * This method updates the provided JSON payload by adding the vehicleId and licensePlate fields,
     * then serializes the JSON object and publishes it to the MQTT topic. The message is retained and uses
     * a QoS level of 1 for at-least-once delivery.
     *
     * @param vehicleId The ID of the vehicle for which mapping data is being sent.
     * @param companyId The ID of the company associated with the vehicle.
     * @param licensePlate The license plate number of the vehicle.
     * @param originalJson The original JSON data containing mapping information.
     *
     * @throws MqttException If an error occurs during MQTT communication.
     * @throws IOException If an error occurs while processing the JSON data.
     */
    public void mapData(Long vehicleId, Long companyId, String licensePlate, JsonNode originalJson) {
        try {
            ((ObjectNode) originalJson).put("vehicleId", vehicleId);
            ((ObjectNode) originalJson).put("licensePlate", licensePlate);

            String updatedPayload = objectMapper.writeValueAsString(originalJson);

            MqttMessage mqttMessage = new MqttMessage(updatedPayload.getBytes());
            mqttMessage.setQos(1);
            mqttMessage.setRetained(true);

            String topic = "mapData/" + companyId;
            mqttClient.publish(topic, mqttMessage);

            //System.out.println("Message sent to topic " + topic + ": " + updatedPayload);
        } catch (MqttException | IOException e) {
            e.printStackTrace();
            System.out.println("Error sending mapData message: " + e.getMessage());
        }
    }

    /**
     * @deprecated
     * This method is deprecated and should no longer be used.
     * Please use the new method (provide its name or description) instead.
     *
     * Sends fuel efficiency data for a specific vehicle to an MQTT topic.
     *
     * This method creates an MQTT message containing a log ID as the payload, then publishes it
     * to the appropriate MQTT topic based on the vehicle ID. The message uses a QoS level of 1 (at-least-once delivery),
     * and it is not retained by the broker. Before sending, the method checks if the MQTT client is connected.
     *
     * @param logId The ID of the log entry related to fuel efficiency.
     * @param vehicleId The ID of the vehicle for which fuel efficiency data is being sent.
     *
     * @throws MqttException If an error occurs during MQTT communication.
     */
    @Deprecated
    public void fuelEfficient(Long logId, Long vehicleId) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setQos(1);
            mqttMessage.setRetained(false);
            mqttMessage.setPayload(logId.toString().getBytes());

            String topic = "fuelEfficient/" + vehicleId;

            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.publish(topic, mqttMessage);
                System.out.println("Message sent to topic " + topic);
            } else {
                System.out.println("MQTT Client is not connected.");
            }
        } catch (MqttException e) {
            System.err.println("Error sending message fuelEfficient: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
