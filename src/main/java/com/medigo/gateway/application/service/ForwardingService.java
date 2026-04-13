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
        log.info("[{}] Forwarding {} {}", traceId, request.getMethod(), path);

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Trace-ID", traceId);

        // Propagar JWT al backend si existe
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            headers.put("Authorization", authHeader);
        }

        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        try {
            ResponseEntity<Object> response = backendClient.send(path, method, headers, body);
            log.info("[{}] Backend response: status={}", traceId, response.getStatusCode());
            return response;
        } catch (Exception e) {
            log.error("[{}] Error forwarding request to backend: {}", traceId, e.getMessage(), e);
            throw e;
        }
    }
}
