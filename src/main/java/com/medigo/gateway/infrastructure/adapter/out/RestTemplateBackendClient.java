package com.medigo.gateway.infrastructure.adapter.out;

import com.medigo.gateway.domain.port.out.BackendClient;
import com.medigo.gateway.infrastructure.config.GatewayProperties;
import com.medigo.gateway.infrastructure.exception.BackendUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Adaptador de salida: llama al backend MediGo usando RestTemplate
 * con Circuit Breaker y Retry de Resilience4j.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RestTemplateBackendClient implements BackendClient {

    private final RestTemplate restTemplate;
    private final GatewayProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    @CircuitBreaker(name = "backendCB", fallbackMethod = "fallback")
    @Retry(name = "backendRetry")
    public ResponseEntity<Object> send(String path, HttpMethod method,
                                       Map<String, String> headers, Object body) {
        String url = properties.getBackend().getBaseUrl() + path;
        HttpHeaders httpHeaders = buildHeaders(headers);
        HttpEntity<Object> entity = new HttpEntity<>(body, httpHeaders);

        log.debug("Backend call: {} {}", method, url);
        try {
            return restTemplate.exchange(url, method, entity, Object.class);
        } catch (HttpStatusCodeException ex) {
            Object responseBody = parseBackendErrorBody(ex);
            log.warn("Backend responded with status {} for {} {}", ex.getStatusCode(), method, path);
            return ResponseEntity.status(ex.getStatusCode()).body(responseBody);
        }
    }

    /**
     * Fallback del circuit breaker: retorna 503.
     */
    public ResponseEntity<Object> fallback(String path, HttpMethod method,
                                           Map<String, String> headers, Object body,
                                           Throwable ex) {
        log.warn("Circuit breaker open for path: {}. Cause: {}", path, ex.getMessage());
        throw new BackendUnavailableException("Backend no disponible temporalmente");
    }

    private HttpHeaders buildHeaders(Map<String, String> extraHeaders) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        extraHeaders.forEach(h::set);
        return h;
    }

    private Object parseBackendErrorBody(HttpStatusCodeException ex) {
        String rawBody = ex.getResponseBodyAsString();
        if (rawBody == null || rawBody.isBlank()) {
            return Map.of("message", ex.getStatusText());
        }

        try {
            return objectMapper.readValue(rawBody, Object.class);
        } catch (Exception ignored) {
            return Map.of("message", ex.getStatusText(), "details", rawBody);
        }
    }
}
