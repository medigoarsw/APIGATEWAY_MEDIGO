package com.medigo.gateway.domain.port.out;

/**
 * Puerto de salida: verificación de rate limiting.
 */
public interface RateLimitPort {
    boolean isAllowed(String key, int maxPerMinute);
}
