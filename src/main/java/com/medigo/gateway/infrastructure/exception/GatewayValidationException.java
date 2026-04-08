package com.medigo.gateway.infrastructure.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción de validación de negocio en el gateway.
 */
public class GatewayValidationException extends RuntimeException {

    private final HttpStatus status;

    public GatewayValidationException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }

    public GatewayValidationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
