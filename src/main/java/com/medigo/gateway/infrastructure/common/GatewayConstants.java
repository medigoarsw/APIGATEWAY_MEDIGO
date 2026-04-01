package com.medigo.gateway.infrastructure.common;

/**
 * Constantes del gateway (sin magic numbers).
 */
public final class GatewayConstants {

    private GatewayConstants() {}

    public static final String TRACE_HEADER   = "X-Trace-ID";
    public static final String API_VERSION     = "v1";
    public static final int    HTTP_TOO_MANY   = 429;
    public static final int    CIRCUIT_OPEN    = 503;
}
