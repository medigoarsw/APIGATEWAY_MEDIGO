package com.medigo.gateway.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propiedades tipadas del gateway desde application.yml.
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {

    private BackendProperties backend = new BackendProperties();
    private JwtProperties jwt = new JwtProperties();
    private RateLimitProperties rateLimit = new RateLimitProperties();

    @Data
    public static class BackendProperties {
        private String baseUrl = "http://localhost:8080";
        private int timeoutSeconds = 30;
    }

    @Data
    public static class JwtProperties {
        private String secret;
        private long expirationMs = 86_400_000L;
    }

    @Data
    public static class RateLimitProperties {
        private int globalPerMinute = 100;
        private int userPerMinute = 500;
        private int bidPerMinute = 10;
    }
}
