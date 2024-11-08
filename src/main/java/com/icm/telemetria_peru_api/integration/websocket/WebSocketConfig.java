package com.icm.telemetria_peru_api.integration.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebSocketService webSocketService;

    public WebSocketConfig(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler(webSocketService), "/ws")
                .setAllowedOrigins("*");
    }
}
