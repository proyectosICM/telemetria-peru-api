package com.icm.telemetria_peru_api.integration.mqtt;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Component responsible for subscribing to MQTT topics and processing received messages.
 * Utilizes the Paho MQTT client library for MQTT communication.
 */
@Component
@RequiredArgsConstructor
public class MqttSubscriber {
    private final IMqttClient mqttClient;
    private final MqttHandler mqttHandler;

    @Value("${mqtt.topics:data,status,prueba}")
    private String mqttTopics;


    @PostConstruct
    public void init() {
        subscribeToTopics(mqttTopics.split("\\s*,\\s*"));
    }

    public void subscribeToTopic(String topic) {
        try {
            mqttClient.subscribe(topic, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    //System.out.println("Mensaje MQTT recibido en el tema " + topic + ": " + payload);
                    mqttHandler.processJsonPayload(payload);
                }
            });
        } catch (MqttException e) {
            System.out.println("Error " + e);
            e.printStackTrace();

        }
    }

    public void subscribeToTopics(String[] topics) {
        Arrays.stream(topics)
                .map(String::trim)
                .filter(topic -> !topic.isBlank())
                .forEach(this::subscribeToTopic);
    }
}
