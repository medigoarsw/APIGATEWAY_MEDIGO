package com.medigo.gateway.infrastructure.security;

import com.medigo.gateway.domain.model.UserClaims;
import com.medigo.gateway.domain.port.out.JwtPort;
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
        if (StringUtils.hasText(token) && jwtPort.isValid(token)) {
            UserClaims claims = jwtPort.validateAndExtract(token);
            String role = "ROLE_" + claims.getRole();

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            claims, null,
                            List.of(new SimpleGrantedAuthority(role)));

            SecurityContextHolder.getContext().setAuthentication(auth);
            log.debug("[{}] Usuario autenticado: {} rol: {}",
                    traceId, claims.getUsername(), claims.getRole());
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
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        // Segundo del query param (WebSocket)
        return req.getParameter("token");
    }
}
