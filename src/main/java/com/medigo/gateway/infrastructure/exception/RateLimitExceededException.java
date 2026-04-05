package com.medigo.gateway.infrastructure.exception;

/**
 * Excepción lanzada cuando se supera el rate limit.
 */
public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) { super(message); }
}
