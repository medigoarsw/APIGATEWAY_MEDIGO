package com.medigo.gateway.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad de auditoría: registra todas las peticiones procesadas por el Gateway.
 * Tabla: audit_logs
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_created_at", columnList = "created_at"),
        @Index(name = "idx_endpoint", columnList = "endpoint"),
        @Index(name = "idx_status_code", columnList = "status_code")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID del usuario que realizó la petición (null si es petición pública).
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * IP del cliente.
     */
    @Column(name = "client_ip", length = 45)
    private String clientIp;

    /**
     * Método HTTP: GET, POST, PUT, DELETE, etc.
     */
    @Column(name = "method", length = 10)
    private String method;

    /**
     * Endpoint llamado: /api/orders/confirm, /api/medications/search, etc.
     */
    @Column(name = "endpoint", length = 255)
    private String endpoint;

    /**
     * Parámetros de query (opcional, para auditoría adicional).
     */
    @Column(name = "query_params", columnDefinition = "TEXT")
    private String queryParams;

    /**
     * Body de la petición (opcional, para auditoría adicional).
     */
    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;

    /**
     * Código HTTP de respuesta: 200, 401, 404, 500, etc.
     */
    @Column(name = "status_code")
    private Integer statusCode;

    /**
     * Body de la respuesta (opcional, solo errores).
     */
    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;

    /**
     * Tiempo de ejecución en milisegundos.
     */
    @Column(name = "duration_ms")
    private Long durationMs;

    /**
     * Timestamp de creación del log.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Trace ID para correlacionar peticiones en logs distribuidos.
     */
    @Column(name = "trace_id", length = 100)
    private String traceId;

    /**
     * Callback pre-insert para establecer timestamp automáticamente.
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
