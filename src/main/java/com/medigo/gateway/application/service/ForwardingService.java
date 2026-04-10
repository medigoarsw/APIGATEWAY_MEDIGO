package com.medigo.gateway.application.service;

import com.medigo.gateway.domain.port.in.ForwardingUseCase;
import com.medigo.gateway.domain.port.out.BackendClient;
import com.medigo.gateway.infrastructure.common.TraceIdHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de forwarding genérico: propaga peticiones HTTP al backend
 * con circuit breaker y trace ID.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ForwardingService implements ForwardingUseCase {

    private final BackendClient backendClient;

    @Override
    public ResponseEntity<Object> forward(String path, HttpServletRequest request, Object body) {
        String traceId = TraceIdHolder.get();
        log.info("[{}] Gateway ForwardingService: Forwarding {} to {}", traceId, request.getMethod(), path);

        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("X-Trace-ID", traceId);

            String authHeader = request.getHeader("Authorization");
            if (authHeader != null) {
                headers.put("Authorization", authHeader);
            }

            HttpMethod method = HttpMethod.valueOf(request.getMethod());
            return backendClient.send(path, method, headers, body);
        } catch (Exception e) {
            log.error("[{}] Gateway ForwardingService: Error during forwarding: {}", traceId, e.getMessage(), e);
            throw e;
        }
    }
}
