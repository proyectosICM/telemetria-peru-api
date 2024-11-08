package com.icm.telemetria_peru_api.integration.websocket;

import org.springframework.stereotype.Service;

@Service
public class WebSocketService {
    public String processMessage(String message) {
        // Lógica de negocio para procesar el mensaje
        System.out.println(message);
        return "Procesado: " + message;
    }
}
