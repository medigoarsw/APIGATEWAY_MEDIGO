package com.medigo.gateway.infrastructure.exception;

/**
 * Excepción de validación de negocio en el gateway.
 */
public class GatewayValidationException extends RuntimeException {
    public GatewayValidationException(String message) { super(message); }
}
