package com.medigo.gateway.infrastructure.interceptor;

import com.medigo.gateway.domain.model.UserClaims;
import com.medigo.gateway.domain.port.out.RateLimitPort;
import com.medigo.gateway.infrastructure.common.GatewayConstants;
import com.medigo.gateway.infrastructure.config.GatewayProperties;
import com.medigo.gateway.infrastructure.exception.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor de rate limiting: global por IP y por usuario autenticado.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitPort rateLimitPort;
    private final GatewayProperties properties;

    @Override
    public boolean preHandle(HttpServletRequest req,
                             HttpServletResponse res,
                             Object handler) {

        String ip = req.getRemoteAddr();
        log.info("Rate limiting check for IP: {}", ip);

        // Rate limit global por IP
        if (!rateLimitPort.isAllowed("ip:" + ip,
                properties.getRateLimit().getGlobalPerMinute())) {
            log.warn("Rate limit exceeded for IP: {}", ip);
            throw new RateLimitExceededException("Límite de peticiones por IP excedido");
        }

        // Rate limit por usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserClaims claims) {
            int limit = isBidEndpoint(req)
                    ? properties.getRateLimit().getBidPerMinute()
                    : properties.getRateLimit().getUserPerMinute();

            log.info("Rate limiting check for user: {} with limit: {}", claims.getUserId(), limit);

            if (!rateLimitPort.isAllowed("user:" + claims.getUserId(), limit)) {
                log.warn("Rate limit exceeded for user: {}", claims.getUserId());
                throw new RateLimitExceededException("Límite de peticiones por usuario excedido");
            }
        }

        return true;
    }

    private boolean isBidEndpoint(HttpServletRequest req) {
        return req.getMethod().equalsIgnoreCase("POST")
                && req.getRequestURI().matches(".*/auctions/\\d+/bids.*");
    }
}
