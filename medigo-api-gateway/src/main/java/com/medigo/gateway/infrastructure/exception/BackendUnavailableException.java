package com.medigo.gateway.infrastructure.exception;

/**
 * Excepción lanzada cuando el circuit breaker está abierto.
 */
public class BackendUnavailableException extends RuntimeException {
    public BackendUnavailableException(String message) { super(message); }
}
