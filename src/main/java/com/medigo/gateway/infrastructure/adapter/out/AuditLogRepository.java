package com.medigo.gateway.infrastructure.adapter.out;

import com.medigo.gateway.domain.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio de auditoría: acceso a datos de tabla audit_logs.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Obtener logs por usuario en rango de fechas.
     */
    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId AND a.createdAt >= :from AND a.createdAt <= :to ORDER BY a.createdAt DESC")
    List<AuditLog> findByUserIdAndDateRange(@Param("userId") Long userId,
                                            @Param("from") LocalDateTime from,
                                            @Param("to") LocalDateTime to);

    /**
     * Obtener logs por endpoint.
     */
    @Query("SELECT a FROM AuditLog a WHERE a.endpoint = :endpoint ORDER BY a.createdAt DESC")
    List<AuditLog> findByEndpoint(@Param("endpoint") String endpoint);

    /**
     * Obtener logs por código de estado (ej: 401, 429, 500).
     */
    @Query("SELECT a FROM AuditLog a WHERE a.statusCode = :statusCode ORDER BY a.createdAt DESC")
    List<AuditLog> findByStatusCode(@Param("statusCode") Integer statusCode);

    /**
     * Obtener últimos N logs.
     */
    @Query("SELECT a FROM AuditLog a ORDER BY a.createdAt DESC LIMIT :limit")
    List<AuditLog> findLatest(@Param("limit") int limit);
}
