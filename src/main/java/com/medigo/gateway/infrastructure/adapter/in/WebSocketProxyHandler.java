package com.medigo.gateway.infrastructure.adapter.in;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class WebSocketProxyHandler extends AbstractWebSocketHandler implements SubProtocolCapable {

    private final String backendWsUrl;
    private final Map<String, WebSocketSession> backendSessions = new ConcurrentHashMap<>();
    private final Map<String, Queue<WebSocketMessage<?>>> pendingMessages = new ConcurrentHashMap<>();

    public WebSocketProxyHandler(String backendWsUrl) {
        this.backendWsUrl = backendWsUrl;
    }

    @Override
    public List<String> getSubProtocols() {
        return List.of("v10.stomp", "v11.stomp", "v12.stomp");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession clientSession) throws Exception {
        String id = clientSession.getId();
        log.info("Gateway: Nueva conexión de cliente [{}]. Conectando al backend...", id);
        
        pendingMessages.put(id, new ConcurrentLinkedQueue<>());

        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        List<String> protocols = clientSession.getHandshakeHeaders().get("Sec-WebSocket-Protocol");
        if (protocols != null) headers.setSecWebSocketProtocol(protocols);

        client.execute(new AbstractWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession backendSession) throws Exception {
                log.info("Gateway: Conexión backend lista para [{}]. Enviando mensajes en espera...", id);
                backendSessions.put(id, backendSession);
                
                Queue<WebSocketMessage<?>> queue = pendingMessages.get(id);
                if (queue != null) {
                    while (!queue.isEmpty() && backendSession.isOpen()) {
                        backendSession.sendMessage(queue.poll());
                    }
                }
            }

            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                if (clientSession.isOpen()) clientSession.sendMessage(message);
            }

            @Override
            protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
                if (clientSession.isOpen()) clientSession.sendMessage(message);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                if (clientSession.isOpen()) clientSession.close(status);
            }
        }, headers, new URI(backendWsUrl));
    }

    @Override
    protected void handleTextMessage(WebSocketSession clientSession, TextMessage message) throws Exception {
        forward(clientSession, message);
    }

    private void forward(WebSocketSession clientSession, WebSocketMessage<?> message) throws Exception {
        WebSocketSession backend = backendSessions.get(clientSession.getId());
        if (backend != null && backend.isOpen()) {
            backend.sendMessage(message);
        } else {
            Queue<WebSocketMessage<?>> queue = pendingMessages.get(clientSession.getId());
            if (queue != null) queue.add(message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession clientSession, CloseStatus status) throws Exception {
        WebSocketSession backend = backendSessions.remove(clientSession.getId());
        pendingMessages.remove(clientSession.getId());
        if (backend != null && backend.isOpen()) backend.close(status);
        log.info("Gateway: Conexión cerrada para [{}].", clientSession.getId());
    }
}
