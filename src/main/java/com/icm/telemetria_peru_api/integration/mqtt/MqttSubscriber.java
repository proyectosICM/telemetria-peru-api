package com.icm.telemetria_peru_api.integration.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
/*
    @Autowired
    private TireSensorService tireSensorService;
    @Autowired
    private IMqttClient mqttClient;


    public void subscribeToTopic(String topic) {
        try {

            mqttClient.subscribe(topic, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                    String payload = new String(message.getPayload());
                    System.out.println("Mensaje MQTT recibido en el tema " + topic + ": " + payload);


                    ObjectMapper objectMapper = new ObjectMapper();

                    try {
                        System.out.println("ds");
                    } catch (JsonProcessingException e) {

                        System.err.println("Error al deserializar el JSON: " + e.getMessage());
                    }
                }
            });
        } catch (MqttException e) {

            e.printStackTrace();
        }
    }*/
}
