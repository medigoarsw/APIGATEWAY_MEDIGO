package com.medigo.gateway.application.service;

import com.medigo.gateway.domain.model.AuditLog;
import com.medigo.gateway.infrastructure.adapter.out.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests de AuditingService: verificar guardado de logs.
 */
@ExtendWith(MockitoExtension.class)
class AuditingServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditingService auditingService;

    @Test
    void testLogRequest_Success() {
        // Given
        AuditLog log = AuditLog.builder()
                .userId(1L)
                .clientIp("192.168.1.1")
                .method("POST")
                .endpoint("/api/orders/confirm")
                .statusCode(200)
                .durationMs(150L)
                .traceId("trace-123")
                .build();

        AuditLog savedLog = AuditLog.builder()
                .id(1L)
                .userId(1L)
                .clientIp("192.168.1.1")
                .method("POST")
                .endpoint("/api/orders/confirm")
                .statusCode(200)
                .durationMs(150L)
                .traceId("trace-123")
                .build();

        when(auditLogRepository.save(log)).thenReturn(savedLog);

        // When
        AuditLog result = auditingService.logRequest(log);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(auditLogRepository).save(log);
    }

    @Test
    void testLogRequest_HandleError() {
        // Given
        AuditLog log = AuditLog.builder()
                .userId(1L)
                .clientIp("192.168.1.1")
                .method("POST")
                .endpoint("/api/orders/confirm")
                .statusCode(200)
                .durationMs(150L)
                .build();

        when(auditLogRepository.save(log))
                .thenThrow(new RuntimeException("DB connection error"));

        // When
        AuditLog result = auditingService.logRequest(log);

        // Then
        // No excepción relanzada, retorna null
        assertThat(result).isNull();
    }
}
