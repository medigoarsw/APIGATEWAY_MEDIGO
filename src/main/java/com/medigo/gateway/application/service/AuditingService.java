package com.medigo.gateway.application.service;

import com.medigo.gateway.domain.model.AuditLog;
import com.medigo.gateway.infrastructure.adapter.out.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de auditoría: registra peticiones HTTP en BD.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuditingService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Guarda un log de auditoría en la BD.
     */
    public AuditLog logRequest(AuditLog auditLog) {
        try {
            AuditLog saved = auditLogRepository.save(auditLog);
            log.debug("Audit log guardado: {} {} - Usuario: {} - Status: {}",
                    auditLog.getMethod(), auditLog.getEndpoint(),
                    auditLog.getUserId() != null ? auditLog.getUserId() : "ANÓNIMO",
                    auditLog.getStatusCode());
            return saved;
        } catch (Exception e) {
            log.error("Error guardando audit log: {}", e.getMessage(), e);
            // No relanzar excepción para no afectar el request original
            return null;
        }
    }

    /**
     * Obtiene logs de un usuario en un rango de fechas.
     */
    public List<AuditLog> getLogsForUser(Long userId, LocalDateTime from, LocalDateTime to) {
        return auditLogRepository.findByUserIdAndDateRange(userId, from, to);
    }

    /**
     * Obtiene logs de un endpoint específico.
     */
    public List<AuditLog> getLogsForEndpoint(String endpoint) {
        return auditLogRepository.findByEndpoint(endpoint);
    }

    /**
     * Obtiene logs por código de estado.
     */
    public List<AuditLog> getLogsByStatusCode(Integer statusCode) {
        return auditLogRepository.findByStatusCode(statusCode);
    }

    /**
     * Obtiene los últimos N logs.
     */
    public List<AuditLog> getLatestLogs(int limit) {
        return auditLogRepository.findLatest(limit);
    }
}
