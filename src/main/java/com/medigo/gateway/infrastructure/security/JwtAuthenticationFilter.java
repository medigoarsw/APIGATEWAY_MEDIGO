package com.medigo.gateway.infrastructure.security;

import com.medigo.gateway.domain.model.UserClaims;
import com.medigo.gateway.domain.port.out.JwtPort;
import com.medigo.gateway.infrastructure.common.RoleMapper;
import com.medigo.gateway.infrastructure.common.TraceIdHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Filtro JWT: extrae y valida el token en cada petición,
 * propaga X-Trace-ID y autentica en el SecurityContext.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtPort jwtPort;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/ws");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        // Generar o propagar Trace ID
        String traceId = request.getHeader("X-Trace-ID");
        if (!StringUtils.hasText(traceId)) {
            traceId = UUID.randomUUID().toString();
        }
        TraceIdHolder.set(traceId);
        response.setHeader("X-Trace-ID", traceId);

        // Procesar JWT
        String token = extractToken(request);
        log.info("[{}] JWT Filter: Extracted token beginning with: {}", traceId, 
                 (token != null && token.length() > 10) ? token.substring(0, 10) : token);

        if (!StringUtils.hasText(token)) {
            log.warn("[{}] Request sin token: {} {}", traceId, request.getMethod(), request.getRequestURI());
        }
        
        // Soporte para tokens de desarrollo (fake-jwt)
        boolean isFake = StringUtils.hasText(token) && token.startsWith("fake-jwt");
        log.info("[{}] JWT Filter: isFake={}, isValid={}", traceId, isFake, 
                 StringUtils.hasText(token) ? jwtPort.isValid(token) : "false");
        
        if (StringUtils.hasText(token) && (isFake || jwtPort.isValid(token))) {
            try {
                UserClaims claims;
                if (isFake) {
                    claims = extractFakeClaims(token);
                    log.info("[{}] JWT Filter: Extracted Fake Claims: role={}", traceId, claims.getRole());
                } else {
                    claims = jwtPort.validateAndExtract(token);
                    log.info("[{}] JWT Filter: Extracted Real Claims: role={}", traceId, claims.getRole());
                }
                
                // Validar que claims no sea null
                if (claims == null) {
                    log.error("[{}] Claims extraidas son null del token", traceId);
                    chain.doFilter(request, response);
                    return;
                }
                
                String rawRole = claims.getRole();
                if (rawRole == null || rawRole.isBlank()) {
                    log.warn("[{}] Token contiene role vacio, asignando AFFILIATE por defecto", traceId);
                    rawRole = "AFFILIATE";
                }

                String canonicalRole = RoleMapper.toCanonical(rawRole);
                log.info("[{}] JWT Filter: rawRole={} -> canonicalRole={}", traceId, rawRole, canonicalRole);
                String grantedAuthority = "ROLE_" + canonicalRole;

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                claims, null,
                                List.of(new SimpleGrantedAuthority(grantedAuthority)));

                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("[{}] Usuario autenticado: username={} rawRole={} canonicalRole={} authority={}",
                         traceId, claims.getUsername(), rawRole, canonicalRole, grantedAuthority);
            } catch (Exception e) {
                log.error("[{}] Error validando JWT: {}", traceId, e.getMessage(), e);
                // No configurar autenticación, continuar como anonimo
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            TraceIdHolder.clear();
        }
    }

    private String extractToken(HttpServletRequest req) {
        // Primero del header
        String header = req.getHeader("Authorization");
        String token = null;

        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            token = header.substring(BEARER_PREFIX.length()).trim();
        } else {
            // Segundo del query param (WebSocket)
            token = req.getParameter("token");
        }

        // Seguridad extra: evitar strings "null" o "undefined" literales
        if ("null".equalsIgnoreCase(token) || "undefined".equalsIgnoreCase(token)) {
            return null;
        }

        return token;
    }

    private UserClaims extractFakeClaims(String token) {
        String[] parts = token.split("\\.");
        // format esperado: fake-jwt.userId.ROLE.timestamp
        String role = (parts.length >= 3) ? parts[2].toUpperCase() : "AFFILIATE";
        
        // Traducciones comunes para facilitar desarrollo
        if ("AFILIADO".equals(role)) role = "AFFILIATE";
        if ("REPARTIDOR".equals(role)) role = "DELIVERY";
        if ("USER".equals(role)) role = "USUARIO";

        return UserClaims.builder()
                .userId(parts.length > 1 ? parts[1] : "0")
                .username("DevUser")
                .email("dev@medigo.co")
                .role(role)
                .build();
    }
}
