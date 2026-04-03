package com.medigo.gateway.infrastructure.interceptor;

import com.medigo.gateway.application.service.AuditingService;
import com.medigo.gateway.domain.model.AuditLog;
import com.medigo.gateway.domain.model.UserClaims;
import com.medigo.gateway.infrastructure.common.TraceIdHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;
import java.util.Objects;

/**
 * Interceptor de auditoría: registra todas las peticiones HTTP en BD (audit_logs).
 * Se ejecuta después de que la petición haya sido procesada.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLoggingInterceptor implements HandlerInterceptor {

    private final AuditingService auditingService;

    /**
     * Post-handle: ejecutado después de que el controller procesa la petición.
     * Guarda el log en BD.
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Object handler,
                                 Exception ex) {

        try {
            // Extraer tiempo de inicio (guardado en preHandle)
            Object startAttr = request.getAttribute("auditStartTime");
            long startTime;
            if (startAttr instanceof Long) {
                startTime = (Long) startAttr;
            } else {
                startTime = System.currentTimeMillis();
            }
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Extraer datos de la petición
            String method = request.getMethod();
            String endpoint = request.getRequestURI();
            String queryParams = request.getQueryString();
            String clientIp = getClientIp(request);
            Integer statusCode = response.getStatus();
            String traceId = TraceIdHolder.get();

            // Extraer user ID si está autenticado
            Long userId = null;
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UserClaims claims) {
                userId = claims.getUserId();
            }

            // Crear log
            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .clientIp(clientIp)
                    .method(method)
                    .endpoint(endpoint)
                    .queryParams(queryParams)
                    .statusCode(statusCode)
                    .durationMs(duration)
                    .traceId(traceId)
                    .build();

            // Guardar en BD (async para no bloquear)
            auditingService.logRequest(auditLog);

            log.debug("[{}] Audit: {} {} - Status: {} - Duration: {}ms - User: {}",
                    traceId, method, endpoint, statusCode, duration,
                    userId != null ? userId : "ANÓNIMO");

        } catch (Exception e) {
            log.error("Error en AuditLoggingInterceptor: {}", e.getMessage(), e);
            // No relanzar excepción para no afectar el request
        }
    }

    /**
     * Pre-handle: ejecutado antes de procesar la petición.
     * Registra el timestamp de inicio.
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        request.setAttribute("auditStartTime", System.currentTimeMillis());
        return true;
    }

    /**
     * Obtiene la IP del cliente considerando proxies (X-Forwarded-For, etc).
     */
    private String getClientIp(HttpServletRequest request) {
        // Considerar proxies
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
