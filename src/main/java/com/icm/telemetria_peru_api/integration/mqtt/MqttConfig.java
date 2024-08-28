package com.icm.telemetria_peru_api.integration.mqtt;


import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttConfig {
    @Value("${mqtt.serverUri}")
    private String serverUri;

    @Bean
    public MqttClient mqttClient() throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{serverUri});
        MqttClient client = new MqttClient(serverUri, MqttClient.generateClientId(), new MemoryPersistence());
        client.connect(options);
        return client;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{serverUri});
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MqttPahoMessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(MqttClient.generateClientId(), mqttClientFactory());
        messageHandler.setAsync(true); // Si quieres asincronía
        messageHandler.setDefaultTopic("defaultTopic"); // Configura el tema predeterminado
        return messageHandler;
    }
}
