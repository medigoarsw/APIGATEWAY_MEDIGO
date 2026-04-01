package com.medigo.gateway.domain.port.out;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Puerto de salida: cliente HTTP hacia el backend MediGo.
 */
public interface BackendClient {
    ResponseEntity<Object> send(String path, HttpMethod method,
                                Map<String, String> headers, Object body);
}
