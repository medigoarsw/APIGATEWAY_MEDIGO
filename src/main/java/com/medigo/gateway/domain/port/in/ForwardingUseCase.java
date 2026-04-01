package com.medigo.gateway.domain.port.in;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

/**
 * Puerto de entrada: forwarding genérico de peticiones HTTP al backend.
 */
public interface ForwardingUseCase {
    ResponseEntity<Object> forward(String path, HttpServletRequest request, Object body);
}
