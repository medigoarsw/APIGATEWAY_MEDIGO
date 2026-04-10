package com.medigo.gateway.infrastructure.config;

import com.medigo.gateway.infrastructure.adapter.in.WebSocketProxyHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Configuración WebSocket para el gateway.
 * Proxy de tráfico WebSocket al backend.
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final GatewayProperties properties;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Obtenemos la URL del backend y la convertimos a ws://
        String backendUrl = properties.getBackend().getBaseUrl();
        String wsUrl = backendUrl.replace("http://", "ws://").replace("https://", "wss://") + "/ws";
        
        System.out.println("CONFIG: WebSocket Proxy initialized pointing to backend: " + wsUrl);
        
        registry.addHandler(new WebSocketProxyHandler(wsUrl), "/ws")
                .setAllowedOriginPatterns("*");
    }
}
