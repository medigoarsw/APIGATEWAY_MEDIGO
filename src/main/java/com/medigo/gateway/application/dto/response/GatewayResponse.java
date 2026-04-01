package com.medigo.gateway.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Envelope estándar de respuesta del gateway.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GatewayResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String traceId;
    private String apiVersion;
    private Instant timestamp;

    public static <T> GatewayResponse<T> ok(T data, String traceId) {
        return GatewayResponse.<T>builder()
                .success(true)
                .data(data)
                .traceId(traceId)
                .apiVersion("v1")
                .timestamp(Instant.now())
                .build();
    }

    public static <T> GatewayResponse<T> error(String message, String traceId) {
        return GatewayResponse.<T>builder()
                .success(false)
                .message(message)
                .traceId(traceId)
                .apiVersion("v1")
                .timestamp(Instant.now())
                .build();
    }
}
