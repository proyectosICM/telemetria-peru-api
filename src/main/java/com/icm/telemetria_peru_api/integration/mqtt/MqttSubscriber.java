package com.icm.telemetria_peru_api.integration.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icm.telemetria_peru_api.repositories.SpeedExcessLoggerRepository;
import com.icm.telemetria_peru_api.repositories.VehicleRepository;
import jakarta.annotation.PostConstruct;
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
    private IMqttClient mqttClient;

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private SpeedExcessLoggerRepository speedExcessLoggerRepository;
    @Autowired
    private MqttMessagePublisher mqttMessagePublisher;

    @Autowired
    private MqttHandler mqttHandler;

    private ObjectMapper objectMapper = new ObjectMapper();


    @PostConstruct
    public void init() {
        String[] topics = {"data", "status", "prueba"};
        mqttMessagePublisher = new MqttMessagePublisher(mqttClient);
        subscribeToTopics(topics);
        subscribeToTopic("prueba");
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
