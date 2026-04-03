package com.medigo.gateway.infrastructure.adapter.out;

import com.medigo.gateway.domain.model.AuditLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de AuditLogRepository: verificar acceso a datos de audit_logs.
 */
@DataJpaTest
@ActiveProfiles("test")
class AuditLogRepositoryTest {

    @Autowired
    private AuditLogRepository auditLogRepository;

    private AuditLog testLog;

    @BeforeEach
    void setUp() {
        testLog = AuditLog.builder()
                .userId(1L)
                .clientIp("192.168.1.1")
                .method("POST")
                .endpoint("/api/orders/confirm")
                .statusCode(200)
                .durationMs(150L)
                .traceId("trace-123")
                .build();
    }

    @Test
    void testSaveAndFindAuditLog() {
        // Given
        AuditLog saved = auditLogRepository.save(testLog);

        // When
        AuditLog found = auditLogRepository.findById(saved.getId()).orElse(null);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getUserId()).isEqualTo(1L);
        assertThat(found.getEndpoint()).isEqualTo("/api/orders/confirm");
        assertThat(found.getStatusCode()).isEqualTo(200);
    }

    @Test
    void testFindByUserId() {
        // Given
        auditLogRepository.save(testLog);
        AuditLog log2 = AuditLog.builder()
                .userId(2L)
                .clientIp("192.168.1.2")
                .method("GET")
                .endpoint("/api/medications/search")
                .statusCode(200)
                .durationMs(50L)
                .traceId("trace-124")
                .build();
        auditLogRepository.save(log2);

        // When
        List<AuditLog> logs = auditLogRepository.findByUserIdAndDateRange(
                1L,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1)
        );

        // Then
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getUserId()).isEqualTo(1L);
    }

    @Test
    void testFindByEndpoint() {
        // Given
        auditLogRepository.save(testLog);
        AuditLog log2 = AuditLog.builder()
                .userId(2L)
                .clientIp("192.168.1.2")
                .method("POST")
                .endpoint("/api/orders/confirm")
                .statusCode(400)
                .durationMs(75L)
                .traceId("trace-125")
                .build();
        auditLogRepository.save(log2);

        // When
        List<AuditLog> logs = auditLogRepository.findByEndpoint("/api/orders/confirm");

        // Then
        assertThat(logs).hasSize(2);
        assertThat(logs).allMatch(log -> log.getEndpoint().equals("/api/orders/confirm"));
    }

    @Test
    void testFindByStatusCode() {
        // Given
        auditLogRepository.save(testLog); // 200
        AuditLog log401 = AuditLog.builder()
                .userId(null)
                .clientIp("192.168.1.3")
                .method("GET")
                .endpoint("/api/auth/me")
                .statusCode(401)
                .durationMs(10L)
                .traceId("trace-126")
                .build();
        auditLogRepository.save(log401);

        // When
        List<AuditLog> logs = auditLogRepository.findByStatusCode(200);

        // Then
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getStatusCode()).isEqualTo(200);
    }

    @Test
    void testFindLatest() {
        // Given
        for (int i = 0; i < 5; i++) {
            auditLogRepository.save(AuditLog.builder()
                    .userId((long) i)
                    .clientIp("192.168.1." + i)
                    .method("GET")
                    .endpoint("/api/medications/search")
                    .statusCode(200)
                    .durationMs((long) i * 10)
                    .traceId("trace-" + i)
                    .build());
        }

        // When
        List<AuditLog> latest = auditLogRepository.findLatest(3);

        // Then
        assertThat(latest).hasSize(3);
    }
}
