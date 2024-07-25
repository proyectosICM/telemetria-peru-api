package com.icm.telemetria_peru_api.integration.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icm.tiremanagementapi.models.TireSensorModel;
import com.icm.tiremanagementapi.services.TireSensorService;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Component responsible for subscribing to MQTT topics and processing received messages.
 * Utilizes the Paho MQTT client library for MQTT communication.
 */
@Component
public class MqttSubscriber {

    @Autowired
    private TireSensorService tireSensorService;
    @Autowired
    private IMqttClient mqttClient;

    /**
     * Subscribes to a specified MQTT topic and defines how messages received on that topic are processed.
     *
     * @param topic The MQTT topic to subscribe to.
     */
    public void subscribeToTopic(String topic) {
        try {
            /// Subscription to the given MQTT topic
            mqttClient.subscribe(topic, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // Convert message payload to string
                    String payload = new String(message.getPayload());
                    System.out.println("Mensaje MQTT recibido en el tema " + topic + ": " + payload);

                    // Attempt to deserialize the message payload into a TireModel object
                    ObjectMapper objectMapper = new ObjectMapper();

                    try {
                        System.out.println("ds");
                    } catch (JsonProcessingException e) {
                        // Handle JSON parsing errors
                        System.err.println("Error al deserializar el JSON: " + e.getMessage());
                    }
                }
            });
        } catch (MqttException e) {
            // Handle exceptions related to MQTT subscription
            e.printStackTrace();
        }
    }
}
