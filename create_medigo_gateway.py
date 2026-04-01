#!/usr/bin/env python3
"""
MediGo API Gateway - Script de Generación Automática
Ejecutar en PowerShell: python create_medigo_gateway.py
"""

import os
import sys

BASE = "medigo-api-gateway"
PKG  = "src/main/java/com/medigo/gateway"
RES  = "src/main/resources"
TEST = "src/test/java/com/medigo/gateway"

FILES = {}

# ─────────────────────────────────────────────
# pom.xml
# ─────────────────────────────────────────────
FILES["pom.xml"] = """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.5</version>
        <relativePath/>
    </parent>

    <groupId>com.medigo</groupId>
    <artifactId>medigo-api-gateway</artifactId>
    <version>1.0.0</version>
    <name>MediGo API Gateway</name>
    <description>Reverse proxy seguro y resiliente para la plataforma MediGo</description>

    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>2022.0.4</spring-cloud.version>
        <jjwt.version>0.12.3</jjwt.version>
        <resilience4j.version>2.1.0</resilience4j.version>
        <springdoc.version>2.2.0</springdoc.version>
    </properties>

    <dependencies>
        <!-- Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Security -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <!-- WebSocket -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>

        <!-- Redis Reactive -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!-- Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Actuator / Metrics -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>

        <!-- Resilience4j -->
        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-spring-boot3</artifactId>
            <version>${resilience4j.version}</version>
        </dependency>
        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-circuitbreaker</artifactId>
            <version>${resilience4j.version}</version>
        </dependency>
        <dependency>
            <groupId>io.github.resilience4j</groupId>
            <artifactId>resilience4j-ratelimiter</artifactId>
            <version>${resilience4j.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- OpenAPI / Swagger -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>

        <!-- AOP para Resilience4j -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.github.tomakehurst</groupId>
            <artifactId>wiremock-jre8-standalone</artifactId>
            <version>2.35.1</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>testcontainers</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.testcontainers</groupId>
                <artifactId>testcontainers-bom</artifactId>
                <version>1.19.1</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.8.11</version>
                <executions>
                    <execution>
                        <goals><goal>prepare-agent</goal></goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals><goal>report</goal></goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
"""

# ─────────────────────────────────────────────
# application.yml
# ─────────────────────────────────────────────
FILES[f"{RES}/application.yml"] = """server:
  port: 8081

spring:
  application:
    name: medigo-api-gateway

  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      timeout: 2000ms

gateway:
  backend:
    base-url: ${BACKEND_URL:http://localhost:8080}
    timeout-seconds: 30
  jwt:
    secret: ${JWT_SECRET:medigo-super-secret-key-must-be-at-least-256-bits-long}
    expiration-ms: ${JWT_EXPIRATION_MS:86400000}
  rate-limit:
    global-per-minute: 100
    user-per-minute: 500
    bid-per-minute: 10

management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,metrics
  endpoint:
    health:
      show-details: always

resilience4j:
  circuitbreaker:
    instances:
      backendCB:
        registerHealthIndicator: true
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
  retry:
    instances:
      backendRetry:
        maxAttempts: 3
        waitDuration: 500ms
        retryExceptions:
          - org.springframework.web.client.ResourceAccessException

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html

logging:
  level:
    com.medigo.gateway: DEBUG
    org.springframework.security: INFO
"""

# ─────────────────────────────────────────────
# Main Application
# ─────────────────────────────────────────────
FILES[f"{PKG}/MedigoApiGatewayApplication.java"] = """package com.medigo.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del API Gateway de MediGo.
 * Puerto por defecto: 8081
 */
@SpringBootApplication
public class MedigoApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(MedigoApiGatewayApplication.class, args);
    }
}
"""

# ─────────────────────────────────────────────
# DOMAIN - Ports (interfaces hexagonales)
# ─────────────────────────────────────────────
FILES[f"{PKG}/domain/port/in/AuthUseCase.java"] = """package com.medigo.gateway.domain.port.in;

import com.medigo.gateway.application.dto.request.LoginRequest;
import com.medigo.gateway.application.dto.response.LoginResponse;

/**
 * Puerto de entrada: caso de uso de autenticación.
 */
public interface AuthUseCase {
    LoginResponse login(LoginRequest request);
}
"""

FILES[f"{PKG}/domain/port/in/ForwardingUseCase.java"] = """package com.medigo.gateway.domain.port.in;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

/**
 * Puerto de entrada: forwarding genérico de peticiones HTTP al backend.
 */
public interface ForwardingUseCase {
    ResponseEntity<Object> forward(String path, HttpServletRequest request, Object body);
}
"""

FILES[f"{PKG}/domain/port/out/BackendClient.java"] = """package com.medigo.gateway.domain.port.out;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Puerto de salida: cliente HTTP hacia el backend MediGo.
 */
public interface BackendClient {
    ResponseEntity<Object> send(String path, HttpMethod method,
                                Map<String, String> headers, Object body);
}
"""

FILES[f"{PKG}/domain/port/out/JwtPort.java"] = """package com.medigo.gateway.domain.port.out;

import com.medigo.gateway.domain.model.UserClaims;

/**
 * Puerto de salida: operaciones con tokens JWT.
 */
public interface JwtPort {
    String generateToken(UserClaims claims);
    UserClaims validateAndExtract(String token);
    boolean isValid(String token);
}
"""

FILES[f"{PKG}/domain/port/out/RateLimitPort.java"] = """package com.medigo.gateway.domain.port.out;

/**
 * Puerto de salida: verificación de rate limiting.
 */
public interface RateLimitPort {
    boolean isAllowed(String key, int maxPerMinute);
}
"""

# ─────────────────────────────────────────────
# DOMAIN - Models
# ─────────────────────────────────────────────
FILES[f"{PKG}/domain/model/UserClaims.java"] = """package com.medigo.gateway.domain.model;

import lombok.Builder;
import lombok.Data;

/**
 * Modelo de dominio: claims del JWT de MediGo.
 */
@Data
@Builder
public class UserClaims {
    private String userId;
    private String username;
    private String email;
    private String role;
}
"""

FILES[f"{PKG}/domain/model/GatewayRole.java"] = """package com.medigo.gateway.domain.model;

/**
 * Roles soportados por el gateway.
 */
public enum GatewayRole {
    ADMIN, USUARIO, REPARTIDOR
}
"""

# ─────────────────────────────────────────────
# APPLICATION - DTOs
# ─────────────────────────────────────────────
FILES[f"{PKG}/application/dto/request/LoginRequest.java"] = """package com.medigo.gateway.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de solicitud de login.
 */
@Data
public class LoginRequest {

    @NotBlank(message = "Username es requerido")
    private String username;

    @NotBlank(message = "Password es requerido")
    @Size(min = 8, message = "Password debe tener al menos 8 caracteres")
    private String password;
}
"""

FILES[f"{PKG}/application/dto/request/CreateAuctionRequest.java"] = """package com.medigo.gateway.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de creación de subasta.
 */
@Data
public class CreateAuctionRequest {

    @NotNull(message = "medicationId es requerido")
    private Long medicationId;

    @NotNull(message = "branchId es requerido")
    private Long branchId;

    @NotNull @Positive(message = "basePrice debe ser mayor a 0")
    private BigDecimal basePrice;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;
}
"""

FILES[f"{PKG}/application/dto/request/PlaceBidRequest.java"] = """package com.medigo.gateway.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO de oferta en subasta.
 */
@Data
public class PlaceBidRequest {

    @NotNull @Positive(message = "amount debe ser mayor a 0")
    private BigDecimal amount;

    @NotBlank(message = "userName es requerido")
    private String userName;

    @NotNull(message = "userId es requerido")
    private Long userId;
}
"""

FILES[f"{PKG}/application/dto/request/CreateOrderRequest.java"] = """package com.medigo.gateway.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

/**
 * DTO de creación de orden.
 */
@Data
public class CreateOrderRequest {

    @NotNull private Long affiliateId;
    @NotNull private Long branchId;

    @DecimalMin("-90.0") @DecimalMax("90.0")
    private Double lat;

    @DecimalMin("-180.0") @DecimalMax("180.0")
    private Double lng;

    @NotEmpty(message = "items no puede estar vacío")
    private List<Object> items;
}
"""

FILES[f"{PKG}/application/dto/response/LoginResponse.java"] = """package com.medigo.gateway.application.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * DTO de respuesta de login (incluye JWT generado por el gateway).
 */
@Data
@Builder
public class LoginResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String jwtToken;
}
"""

FILES[f"{PKG}/application/dto/response/GatewayResponse.java"] = """package com.medigo.gateway.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Envelope estándar de respuesta del gateway.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GatewayResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String traceId;
    private String apiVersion;
    private Instant timestamp;

    public static <T> GatewayResponse<T> ok(T data, String traceId) {
        return GatewayResponse.<T>builder()
                .success(true)
                .data(data)
                .traceId(traceId)
                .apiVersion("v1")
                .timestamp(Instant.now())
                .build();
    }

    public static <T> GatewayResponse<T> error(String message, String traceId) {
        return GatewayResponse.<T>builder()
                .success(false)
                .message(message)
                .traceId(traceId)
                .apiVersion("v1")
                .timestamp(Instant.now())
                .build();
    }
}
"""

# ─────────────────────────────────────────────
# APPLICATION - Services
# ─────────────────────────────────────────────
FILES[f"{PKG}/application/service/AuthGatewayService.java"] = """package com.medigo.gateway.application.service;

import com.medigo.gateway.application.dto.request.LoginRequest;
import com.medigo.gateway.application.dto.response.LoginResponse;
import com.medigo.gateway.domain.model.UserClaims;
import com.medigo.gateway.domain.port.in.AuthUseCase;
import com.medigo.gateway.domain.port.out.BackendClient;
import com.medigo.gateway.domain.port.out.JwtPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Servicio de autenticación del gateway.
 * Delega credenciales al backend y genera JWT propio.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthGatewayService implements AuthUseCase {

    private final BackendClient backendClient;
    private final JwtPort jwtPort;

    @Override
    public LoginResponse login(LoginRequest request) {
        log.debug("Forwarding login request for user: {}", request.getUsername());

        ResponseEntity<Object> backendResponse = backendClient.send(
                "/api/auth/login", HttpMethod.POST, Map.of(), request
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) backendResponse.getBody();

        if (body == null) {
            throw new IllegalStateException("Backend returned empty body on login");
        }

        UserClaims claims = UserClaims.builder()
                .userId(String.valueOf(body.get("id")))
                .username(String.valueOf(body.get("username")))
                .email(String.valueOf(body.getOrDefault("email", "")))
                .role(String.valueOf(body.getOrDefault("role", "USUARIO")))
                .build();

        String jwt = jwtPort.generateToken(claims);

        return LoginResponse.builder()
                .id(Long.parseLong(claims.getUserId()))
                .username(claims.getUsername())
                .email(claims.getEmail())
                .role(claims.getRole())
                .jwtToken(jwt)
                .build();
    }
}
"""

FILES[f"{PKG}/application/service/ForwardingService.java"] = """package com.medigo.gateway.application.service;

import com.medigo.gateway.domain.port.in.ForwardingUseCase;
import com.medigo.gateway.domain.port.out.BackendClient;
import com.medigo.gateway.infrastructure.common.TraceIdHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de forwarding genérico: propaga peticiones HTTP al backend
 * con circuit breaker y trace ID.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ForwardingService implements ForwardingUseCase {

    private final BackendClient backendClient;

    @Override
    public ResponseEntity<Object> forward(String path, HttpServletRequest request, Object body) {
        String traceId = TraceIdHolder.get();
        log.info("[{}] Forwarding {} {}", traceId, request.getMethod(), path);

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Trace-ID", traceId);

        // Propagar JWT al backend si existe
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            headers.put("Authorization", authHeader);
        }

        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        return backendClient.send(path, method, headers, body);
    }
}
"""

FILES[f"{PKG}/application/service/ValidationService.java"] = """package com.medigo.gateway.application.service;

import com.medigo.gateway.application.dto.request.CreateAuctionRequest;
import com.medigo.gateway.infrastructure.exception.GatewayValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Servicio de validaciones de negocio adicionales (más allá de Bean Validation).
 */
@Slf4j
@Service
public class ValidationService {

    /**
     * Valida reglas de negocio para crear subasta.
     */
    public void validateCreateAuction(CreateAuctionRequest req) {
        if (req.getEndTime().isBefore(req.getStartTime()) ||
            req.getEndTime().isEqual(req.getStartTime())) {
            throw new GatewayValidationException("endTime debe ser posterior a startTime");
        }
        log.debug("CreateAuctionRequest validado correctamente");
    }
}
"""

# ─────────────────────────────────────────────
# INFRASTRUCTURE - Adapters
# ─────────────────────────────────────────────
FILES[f"{PKG}/infrastructure/adapter/out/RestTemplateBackendClient.java"] = """package com.medigo.gateway.infrastructure.adapter.out;

import com.medigo.gateway.domain.port.out.BackendClient;
import com.medigo.gateway.infrastructure.config.GatewayProperties;
import com.medigo.gateway.infrastructure.exception.BackendUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Adaptador de salida: llama al backend MediGo usando RestTemplate
 * con Circuit Breaker y Retry de Resilience4j.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RestTemplateBackendClient implements BackendClient {

    private final RestTemplate restTemplate;
    private final GatewayProperties properties;

    @Override
    @CircuitBreaker(name = "backendCB", fallbackMethod = "fallback")
    @Retry(name = "backendRetry")
    public ResponseEntity<Object> send(String path, HttpMethod method,
                                       Map<String, String> headers, Object body) {
        String url = properties.getBackend().getBaseUrl() + path;
        HttpHeaders httpHeaders = buildHeaders(headers);
        HttpEntity<Object> entity = new HttpEntity<>(body, httpHeaders);

        log.debug("Backend call: {} {}", method, url);
        return restTemplate.exchange(url, method, entity, Object.class);
    }

    /**
     * Fallback del circuit breaker: retorna 503.
     */
    public ResponseEntity<Object> fallback(String path, HttpMethod method,
                                           Map<String, String> headers, Object body,
                                           Throwable ex) {
        log.warn("Circuit breaker open for path: {}. Cause: {}", path, ex.getMessage());
        throw new BackendUnavailableException("Backend no disponible temporalmente");
    }

    private HttpHeaders buildHeaders(Map<String, String> extraHeaders) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        extraHeaders.forEach(h::set);
        return h;
    }
}
"""

FILES[f"{PKG}/infrastructure/adapter/out/JjwtAdapter.java"] = """package com.medigo.gateway.infrastructure.adapter.out;

import com.medigo.gateway.domain.model.UserClaims;
import com.medigo.gateway.domain.port.out.JwtPort;
import com.medigo.gateway.infrastructure.config.GatewayProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Adaptador JWT usando JJWT 0.12.x.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JjwtAdapter implements JwtPort {

    private final GatewayProperties properties;

    @Override
    public String generateToken(UserClaims claims) {
        return Jwts.builder()
                .subject(claims.getUserId())
                .claim("username", claims.getUsername())
                .claim("email", claims.getEmail())
                .claim("role", claims.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() +
                        properties.getJwt().getExpirationMs()))
                .signWith(getKey())
                .compact();
    }

    @Override
    public UserClaims validateAndExtract(String token) {
        Claims c = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return UserClaims.builder()
                .userId(c.getSubject())
                .username(c.get("username", String.class))
                .email(c.get("email", String.class))
                .role(c.get("role", String.class))
                .build();
    }

    @Override
    public boolean isValid(String token) {
        try {
            validateAndExtract(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("JWT invalido: {}", ex.getMessage());
            return false;
        }
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(
                properties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
"""

FILES[f"{PKG}/infrastructure/adapter/out/RedisRateLimitAdapter.java"] = """package com.medigo.gateway.infrastructure.adapter.out;

import com.medigo.gateway.domain.port.out.RateLimitPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Adaptador de rate limiting usando Redis con ventana deslizante de 1 minuto.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisRateLimitAdapter implements RateLimitPort {

    private static final String KEY_PREFIX = "rl:";
    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean isAllowed(String key, int maxPerMinute) {
        String redisKey = KEY_PREFIX + key;
        Long count = redisTemplate.opsForValue().increment(redisKey);

        if (count != null && count == 1) {
            redisTemplate.expire(redisKey, Duration.ofMinutes(1));
        }

        boolean allowed = count != null && count <= maxPerMinute;
        if (!allowed) {
            log.warn("Rate limit excedido para key: {}", key);
        }
        return allowed;
    }
}
"""

# ─────────────────────────────────────────────
# INFRASTRUCTURE - Config
# ─────────────────────────────────────────────
FILES[f"{PKG}/infrastructure/config/GatewayProperties.java"] = """package com.medigo.gateway.infrastructure.config;

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
"""

FILES[f"{PKG}/infrastructure/config/RestTemplateConfig.java"] = """package com.medigo.gateway.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración del RestTemplate con timeout definido en properties.
 */
@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final GatewayProperties properties;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        int ms = properties.getBackend().getTimeoutSeconds() * 1000;
        factory.setConnectTimeout(ms);
        factory.setReadTimeout(ms);
        return new RestTemplate(factory);
    }
}
"""

FILES[f"{PKG}/infrastructure/config/SecurityConfig.java"] = """package com.medigo.gateway.infrastructure.config;

import com.medigo.gateway.infrastructure.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de Spring Security: stateless JWT + RBAC.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Públicos
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/health").permitAll()

                // Solo ADMIN
                .requestMatchers(HttpMethod.POST, "/api/auctions").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/auctions/**").hasRole("ADMIN")
                .requestMatchers("/api/catalog/**").hasRole("ADMIN")

                // ADMIN y USUARIO
                .requestMatchers("/api/auctions/**").hasAnyRole("ADMIN", "USUARIO")
                .requestMatchers("/api/orders/**").hasAnyRole("ADMIN", "USUARIO")

                // ADMIN y REPARTIDOR
                .requestMatchers("/api/logistics/**").hasAnyRole("ADMIN", "REPARTIDOR")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
"""

FILES[f"{PKG}/infrastructure/config/WebSocketConfig.java"] = """package com.medigo.gateway.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración WebSocket STOMP para el gateway.
 * Proxea los mensajes hacia el backend en /ws.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
"""

FILES[f"{PKG}/infrastructure/config/OpenApiConfig.java"] = """package com.medigo.gateway.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI 3 con soporte JWT Bearer.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI medigoOpenAPI() {
        final String schemeName = "BearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("MediGo API Gateway")
                        .version("1.0.0")
                        .description("Reverse proxy seguro para la plataforma MediGo")
                        .contact(new Contact().name("MediGo Team")))
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components().addSecuritySchemes(schemeName,
                        new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
"""

# ─────────────────────────────────────────────
# INFRASTRUCTURE - Security
# ─────────────────────────────────────────────
FILES[f"{PKG}/infrastructure/security/JwtAuthenticationFilter.java"] = """package com.medigo.gateway.infrastructure.security;

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
"""

# ─────────────────────────────────────────────
# INFRASTRUCTURE - Common
# ─────────────────────────────────────────────
FILES[f"{PKG}/infrastructure/common/TraceIdHolder.java"] = """package com.medigo.gateway.infrastructure.common;

/**
 * ThreadLocal para propagar el X-Trace-ID a lo largo de la petición.
 */
public final class TraceIdHolder {

    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    private TraceIdHolder() {}

    public static void set(String traceId) { HOLDER.set(traceId); }
    public static String get()             { return HOLDER.get(); }
    public static void clear()             { HOLDER.remove(); }
}
"""

FILES[f"{PKG}/infrastructure/common/GatewayConstants.java"] = """package com.medigo.gateway.infrastructure.common;

/**
 * Constantes del gateway (sin magic numbers).
 */
public final class GatewayConstants {

    private GatewayConstants() {}

    public static final String TRACE_HEADER   = "X-Trace-ID";
    public static final String API_VERSION     = "v1";
    public static final int    HTTP_TOO_MANY   = 429;
    public static final int    CIRCUIT_OPEN    = 503;
}
"""

# ─────────────────────────────────────────────
# INFRASTRUCTURE - Exception Handling
# ─────────────────────────────────────────────
FILES[f"{PKG}/infrastructure/exception/BackendUnavailableException.java"] = """package com.medigo.gateway.infrastructure.exception;

/**
 * Excepción lanzada cuando el circuit breaker está abierto.
 */
public class BackendUnavailableException extends RuntimeException {
    public BackendUnavailableException(String message) { super(message); }
}
"""

FILES[f"{PKG}/infrastructure/exception/GatewayValidationException.java"] = """package com.medigo.gateway.infrastructure.exception;

/**
 * Excepción de validación de negocio en el gateway.
 */
public class GatewayValidationException extends RuntimeException {
    public GatewayValidationException(String message) { super(message); }
}
"""

FILES[f"{PKG}/infrastructure/exception/RateLimitExceededException.java"] = """package com.medigo.gateway.infrastructure.exception;

/**
 * Excepción lanzada cuando se supera el rate limit.
 */
public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) { super(message); }
}
"""

FILES[f"{PKG}/infrastructure/exception/GlobalExceptionHandler.java"] = """package com.medigo.gateway.infrastructure.exception;

import com.medigo.gateway.application.dto.response.GatewayResponse;
import com.medigo.gateway.infrastructure.common.TraceIdHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Manejador global de excepciones: transforma errores en respuestas estándar.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GatewayResponse<Void>> handleValidation(
            MethodArgumentNotValidException ex) {

        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest()
                .body(GatewayResponse.error("Validación fallida: " + errors, TraceIdHolder.get()));
    }

    @ExceptionHandler(GatewayValidationException.class)
    public ResponseEntity<GatewayResponse<Void>> handleGatewayValidation(
            GatewayValidationException ex) {
        return ResponseEntity.badRequest()
                .body(GatewayResponse.error(ex.getMessage(), TraceIdHolder.get()));
    }

    @ExceptionHandler(BackendUnavailableException.class)
    public ResponseEntity<GatewayResponse<Void>> handleBackendDown(
            BackendUnavailableException ex) {
        log.error("Backend no disponible: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(GatewayResponse.error(ex.getMessage(), TraceIdHolder.get()));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<GatewayResponse<Void>> handleRateLimit(
            RateLimitExceededException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", "60")
                .body(GatewayResponse.error(ex.getMessage(), TraceIdHolder.get()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GatewayResponse<Void>> handleGeneric(Exception ex) {
        log.error("Error no manejado: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError()
                .body(GatewayResponse.error("Error interno del gateway", TraceIdHolder.get()));
    }
}
"""

# ─────────────────────────────────────────────
# INFRASTRUCTURE - Interceptors
# ─────────────────────────────────────────────
FILES[f"{PKG}/infrastructure/interceptor/RateLimitInterceptor.java"] = """package com.medigo.gateway.infrastructure.interceptor;

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

        // Rate limit global por IP
        if (!rateLimitPort.isAllowed("ip:" + ip,
                properties.getRateLimit().getGlobalPerMinute())) {
            throw new RateLimitExceededException("Límite de peticiones por IP excedido");
        }

        // Rate limit por usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserClaims claims) {
            int limit = isBidEndpoint(req)
                    ? properties.getRateLimit().getBidPerMinute()
                    : properties.getRateLimit().getUserPerMinute();

            if (!rateLimitPort.isAllowed("user:" + claims.getUserId(), limit)) {
                throw new RateLimitExceededException("Límite de peticiones por usuario excedido");
            }
        }

        return true;
    }

    private boolean isBidEndpoint(HttpServletRequest req) {
        return req.getMethod().equalsIgnoreCase("POST")
                && req.getRequestURI().matches(".*/auctions/\\\\d+/bids.*");
    }
}
"""

FILES[f"{PKG}/infrastructure/interceptor/WebMvcConfig.java"] = """package com.medigo.gateway.infrastructure.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registro del interceptor de rate limiting en el pipeline MVC.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/api/auth/register");
    }
}
"""

# ─────────────────────────────────────────────
# CONTROLLERS
# ─────────────────────────────────────────────
FILES[f"{PKG}/infrastructure/adapter/in/AuthController.java"] = """package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.application.dto.request.LoginRequest;
import com.medigo.gateway.application.dto.response.GatewayResponse;
import com.medigo.gateway.application.dto.response.LoginResponse;
import com.medigo.gateway.domain.port.in.AuthUseCase;
import com.medigo.gateway.infrastructure.common.TraceIdHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de autenticación: login y registro.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autenticación JWT")
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    @Operation(summary = "Login - genera JWT")
    public ResponseEntity<GatewayResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authUseCase.login(request);
        return ResponseEntity.ok(GatewayResponse.ok(response, TraceIdHolder.get()));
    }
}
"""

FILES[f"{PKG}/infrastructure/adapter/in/AuctionController.java"] = """package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.application.dto.request.CreateAuctionRequest;
import com.medigo.gateway.application.dto.request.PlaceBidRequest;
import com.medigo.gateway.application.service.ValidationService;
import com.medigo.gateway.domain.port.in.ForwardingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de subastas: enruta hacia el backend.
 */
@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
@Tag(name = "Auctions", description = "Gestión de subastas")
@SecurityRequirement(name = "BearerAuth")
public class AuctionController {

    private final ForwardingUseCase forwardingUseCase;
    private final ValidationService validationService;

    @GetMapping
    @Operation(summary = "Listar subastas")
    public ResponseEntity<Object> list(HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions", req, null);
    }

    @GetMapping("/active")
    @Operation(summary = "Subastas activas")
    public ResponseEntity<Object> active(HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/active", req, null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener subasta por ID")
    public ResponseEntity<Object> getById(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/" + id, req, null);
    }

    @PostMapping
    @Operation(summary = "Crear subasta (ADMIN)")
    public ResponseEntity<Object> create(
            @Valid @RequestBody CreateAuctionRequest body, HttpServletRequest req) {
        validationService.validateCreateAuction(body);
        return forwardingUseCase.forward("/api/auctions", req, body);
    }

    @PostMapping("/{id}/bids")
    @Operation(summary = "Colocar puja")
    public ResponseEntity<Object> placeBid(
            @PathVariable Long id,
            @Valid @RequestBody PlaceBidRequest body,
            HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/" + id + "/bids", req, body);
    }

    @GetMapping("/{id}/bids")
    @Operation(summary = "Historial de pujas")
    public ResponseEntity<Object> getBids(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/" + id + "/bids", req, null);
    }
}
"""

FILES[f"{PKG}/infrastructure/adapter/in/CatalogController.java"] = """package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.domain.port.in.ForwardingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller del catálogo de medicamentos.
 */
@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
@Tag(name = "Catalog", description = "Catálogo de medicamentos")
@SecurityRequirement(name = "BearerAuth")
public class CatalogController {

    private final ForwardingUseCase forwardingUseCase;

    @GetMapping("/medications")
    @Operation(summary = "Listar medicamentos")
    public ResponseEntity<Object> list(HttpServletRequest req) {
        return forwardingUseCase.forward("/api/catalog/medications", req, null);
    }

    @GetMapping("/medications/{id}")
    @Operation(summary = "Obtener medicamento por ID")
    public ResponseEntity<Object> getById(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/catalog/medications/" + id, req, null);
    }

    @PostMapping("/medications")
    @Operation(summary = "Crear medicamento (ADMIN)")
    public ResponseEntity<Object> create(@RequestBody Object body, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/catalog/medications", req, body);
    }
}
"""

FILES[f"{PKG}/infrastructure/adapter/in/OrderController.java"] = """package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.application.dto.request.CreateOrderRequest;
import com.medigo.gateway.domain.port.in.ForwardingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de órdenes.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Gestión de órdenes")
@SecurityRequirement(name = "BearerAuth")
public class OrderController {

    private final ForwardingUseCase forwardingUseCase;

    @PostMapping
    @Operation(summary = "Crear orden")
    public ResponseEntity<Object> create(
            @Valid @RequestBody CreateOrderRequest body, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/orders", req, body);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener orden por ID")
    public ResponseEntity<Object> getById(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/orders/" + id, req, null);
    }

    @GetMapping("/affiliate/{affiliateId}")
    @Operation(summary = "Órdenes por afiliado")
    public ResponseEntity<Object> byAffiliate(
            @PathVariable Long affiliateId, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/orders/affiliate/" + affiliateId, req, null);
    }
}
"""

FILES[f"{PKG}/infrastructure/adapter/in/LogisticsController.java"] = """package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.domain.port.in.ForwardingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de logística y ubicación de entregas.
 */
@RestController
@RequestMapping("/api/logistics")
@RequiredArgsConstructor
@Tag(name = "Logistics", description = "Logística de entregas")
@SecurityRequirement(name = "BearerAuth")
public class LogisticsController {

    private final ForwardingUseCase forwardingUseCase;

    @GetMapping("/deliveries/{id}/location")
    @Operation(summary = "Ubicación de entrega en tiempo real")
    public ResponseEntity<Object> location(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/deliveries/" + id + "/location", req, null);
    }

    @PutMapping("/deliveries/{id}/location")
    @Operation(summary = "Actualizar ubicación (REPARTIDOR)")
    public ResponseEntity<Object> updateLocation(
            @PathVariable Long id, @RequestBody Object body, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/deliveries/" + id + "/location", req, body);
    }

    @GetMapping("/deliveries/{id}")
    @Operation(summary = "Estado de entrega")
    public ResponseEntity<Object> status(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/deliveries/" + id, req, null);
    }
}
"""

FILES[f"{PKG}/infrastructure/adapter/in/HealthController.java"] = """package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.application.dto.response.GatewayResponse;
import com.medigo.gateway.infrastructure.common.GatewayConstants;
import com.medigo.gateway.infrastructure.common.TraceIdHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint de salud del gateway.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Health", description = "Estado del gateway")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Health check del gateway")
    public ResponseEntity<GatewayResponse<Map<String, String>>> health() {
        Map<String, String> info = Map.of(
                "status", "UP",
                "service", "MediGo API Gateway",
                "version", GatewayConstants.API_VERSION
        );
        return ResponseEntity.ok(GatewayResponse.ok(info, TraceIdHolder.get()));
    }
}
"""

# ─────────────────────────────────────────────
# TESTS
# ─────────────────────────────────────────────
FILES[f"{TEST}/application/service/AuthGatewayServiceTest.java"] = """package com.medigo.gateway.application.service;

import com.medigo.gateway.application.dto.request.LoginRequest;
import com.medigo.gateway.application.dto.response.LoginResponse;
import com.medigo.gateway.domain.model.UserClaims;
import com.medigo.gateway.domain.port.out.BackendClient;
import com.medigo.gateway.domain.port.out.JwtPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthGatewayServiceTest {

    @Mock BackendClient backendClient;
    @Mock JwtPort jwtPort;
    @InjectMocks AuthGatewayService service;

    @BeforeEach
    void setUp() {
        Map<String, Object> backendBody = Map.of(
                "id", 1, "username", "testuser",
                "email", "test@test.com", "role", "USUARIO");

        when(backendClient.send(eq("/api/auth/login"), eq(HttpMethod.POST), any(), any()))
                .thenReturn(ResponseEntity.ok(backendBody));

        UserClaims claims = UserClaims.builder()
                .userId("1").username("testuser")
                .email("test@test.com").role("USUARIO").build();

        when(jwtPort.generateToken(any())).thenReturn("mocked.jwt.token");
    }

    @Test
    void testLoginReturnsJwtToken() {
        LoginRequest req = new LoginRequest();
        req.setUsername("testuser");
        req.setPassword("password123");

        LoginResponse response = service.login(req);

        assertThat(response.getJwtToken()).isEqualTo("mocked.jwt.token");
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getRole()).isEqualTo("USUARIO");
    }
}
"""

FILES[f"{TEST}/application/service/ValidationServiceTest.java"] = """package com.medigo.gateway.application.service;

import com.medigo.gateway.application.dto.request.CreateAuctionRequest;
import com.medigo.gateway.infrastructure.exception.GatewayValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class ValidationServiceTest {

    private final ValidationService service = new ValidationService();

    @Test
    void testValidCreateAuctionPasses() {
        CreateAuctionRequest req = new CreateAuctionRequest();
        req.setMedicationId(1L);
        req.setBranchId(1L);
        req.setBasePrice(BigDecimal.TEN);
        req.setStartTime(LocalDateTime.now());
        req.setEndTime(LocalDateTime.now().plusHours(2));

        assertThatCode(() -> service.validateCreateAuction(req)).doesNotThrowAnyException();
    }

    @Test
    void testEndTimeBeforeStartTimeThrows() {
        CreateAuctionRequest req = new CreateAuctionRequest();
        req.setMedicationId(1L);
        req.setBranchId(1L);
        req.setBasePrice(BigDecimal.TEN);
        req.setStartTime(LocalDateTime.now().plusHours(2));
        req.setEndTime(LocalDateTime.now());

        assertThatThrownBy(() -> service.validateCreateAuction(req))
                .isInstanceOf(GatewayValidationException.class)
                .hasMessageContaining("endTime");
    }
}
"""

FILES[f"{TEST}/infrastructure/adapter/out/JjwtAdapterTest.java"] = """package com.medigo.gateway.infrastructure.adapter.out;

import com.medigo.gateway.domain.model.UserClaims;
import com.medigo.gateway.infrastructure.config.GatewayProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JjwtAdapterTest {

    private JjwtAdapter adapter;

    @BeforeEach
    void setUp() {
        GatewayProperties props = new GatewayProperties();
        props.getJwt().setSecret(
                "medigo-super-secret-key-must-be-at-least-256-bits-long-for-test");
        props.getJwt().setExpirationMs(86_400_000L);
        adapter = new JjwtAdapter(props);
    }

    @Test
    void testGenerateAndValidateToken() {
        UserClaims claims = UserClaims.builder()
                .userId("42").username("alice")
                .email("alice@medigo.com").role("ADMIN").build();

        String token = adapter.generateToken(claims);
        assertThat(token).isNotBlank();
        assertThat(adapter.isValid(token)).isTrue();
    }

    @Test
    void testExtractClaimsFromToken() {
        UserClaims original = UserClaims.builder()
                .userId("7").username("bob")
                .email("bob@medigo.com").role("USUARIO").build();

        String token = adapter.generateToken(original);
        UserClaims extracted = adapter.validateAndExtract(token);

        assertThat(extracted.getUserId()).isEqualTo("7");
        assertThat(extracted.getRole()).isEqualTo("USUARIO");
    }

    @Test
    void testInvalidTokenReturnsFalse() {
        assertThat(adapter.isValid("invalid.token.here")).isFalse();
    }
}
"""

FILES[f"{TEST}/infrastructure/adapter/in/AuthControllerTest.java"] = """package com.medigo.gateway.infrastructure.adapter.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medigo.gateway.application.dto.response.LoginResponse;
import com.medigo.gateway.domain.port.in.AuthUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean AuthUseCase authUseCase;

    @Test
    @WithMockUser
    void testLoginSuccess() throws Exception {
        LoginResponse resp = LoginResponse.builder()
                .id(1L).username("user").email("u@test.com")
                .role("USUARIO").jwtToken("jwt.token.here").build();

        when(authUseCase.login(any())).thenReturn(resp);

        Map<String, String> body = Map.of("username", "user", "password", "password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.jwtToken").value("jwt.token.here"));
    }

    @Test
    @WithMockUser
    void testLoginInvalidRequest() throws Exception {
        Map<String, String> body = Map.of("username", "", "password", "");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }
}
"""

# ─────────────────────────────────────────────
# README.md
# ─────────────────────────────────────────────
FILES["README.md"] = """# MediGo API Gateway

Reverse proxy **seguro, resiliente e inteligente** para la plataforma MediGo.
Puerto: `8081` | Backend: `8080`

## Stack
- Java 21 · Spring Boot 3.1.5
- JJWT 0.12.3 · Resilience4j · Redis (Upstash)
- PostgreSQL / Supabase (indirecto vía backend)

## Setup Local

```bash
git clone <repo>
cd medigo-api-gateway

# Variables de entorno (o editar application.yml)
export BACKEND_URL=http://localhost:8080
export JWT_SECRET=medigo-super-secret-key-must-be-at-least-256-bits-long
export REDIS_HOST=localhost
export REDIS_PORT=6379

mvn clean install
mvn spring-boot:run
```

## URLs

| Recurso       | URL                                      |
|---------------|------------------------------------------|
| Swagger UI    | http://localhost:8081/swagger-ui.html    |
| OpenAPI JSON  | http://localhost:8081/api-docs           |
| Health        | http://localhost:8081/api/health         |
| Prometheus    | http://localhost:8081/actuator/prometheus|

## Endpoints principales

| Método | Ruta                          | Rol requerido         |
|--------|-------------------------------|------------------------|
| POST   | /api/auth/login               | Público               |
| GET    | /api/auctions                 | ADMIN, USUARIO        |
| POST   | /api/auctions                 | ADMIN                 |
| POST   | /api/auctions/{id}/bids       | ADMIN, USUARIO        |
| GET    | /api/catalog/medications      | ADMIN                 |
| POST   | /api/orders                   | ADMIN, USUARIO        |
| GET    | /api/logistics/deliveries/{id}| ADMIN, REPARTIDOR     |

## WebSocket

```
ws://localhost:8081/ws
Topics: /topic/auction/{id} | /topic/logistics/locations
Auth: ?token=<jwt>
```

## Tests

```bash
mvn test                    # Unit + Integration
mvn jacoco:report           # Cobertura en target/site/jacoco
```

## Variables de entorno

| Variable         | Descripción               | Default                        |
|------------------|---------------------------|--------------------------------|
| BACKEND_URL      | URL del backend MediGo    | http://localhost:8080          |
| JWT_SECRET       | Secret JWT (≥256 bits)    | —                              |
| JWT_EXPIRATION_MS| Expiración JWT en ms      | 86400000 (24h)                 |
| REDIS_HOST       | Host Redis (Upstash)      | localhost                      |
| REDIS_PORT       | Puerto Redis              | 6379                           |
| REDIS_PASSWORD   | Password Redis            | —                              |
"""

FILES["ARCHITECTURE.md"] = """# Arquitectura del Gateway MediGo

## Hexagonal (Ports & Adapters)

```
[Cliente HTTP/WS]
       |
[Infrastructure - in]  ← Controllers (Adapter in)
       |
[Application]          ← Services (Use Cases)
       |
[Domain]               ← Ports (interfaces) + Models
       |
[Infrastructure - out] ← RestTemplate, Redis, JWT (Adapters out)
       |
[Backend MediGo :8080]
```

## Flujo JWT

```
POST /api/auth/login
  → AuthController
  → AuthGatewayService
  → BackendClient → backend :8080/api/auth/login
  ← {id, username, role}
  → JjwtAdapter.generateToken()
  ← {jwtToken: "eyJ..."}
```

## Circuit Breaker

```
RestTemplateBackendClient
  @CircuitBreaker(backendCB)
    OK  → ResponseEntity
    5 fallos / 10s → OPEN
    OPEN → fallback() → 503
    10s → HALF_OPEN → prueba
```
"""

# ─────────────────────────────────────────────
# .gitignore
# ─────────────────────────────────────────────
FILES[".gitignore"] = """target/
.idea/
*.iml
*.class
.env
application-local.yml
"""

# ─────────────────────────────────────────────
# GENERADOR
# ─────────────────────────────────────────────
def create_project():
    print("\\n🚀 Generando MediGo API Gateway...\\n")
    created = 0

    for relative_path, content in FILES.items():
        full_path = os.path.join(BASE, relative_path)
        directory = os.path.dirname(full_path)

        if directory:
            os.makedirs(directory, exist_ok=True)

        with open(full_path, "w", encoding="utf-8") as f:
            f.write(content)

        print(f"  ✅ {relative_path}")
        created += 1

    # Directorios vacíos necesarios
    empty_dirs = [
        f"{PKG}/domain/model",
        f"src/test/resources",
    ]
    for d in empty_dirs:
        path = os.path.join(BASE, d)
        os.makedirs(path, exist_ok=True)

    print(f"\\n✨ Proyecto generado: {os.path.abspath(BASE)}")
    print(f"   Archivos creados : {created}")
    print("\\n📋 Próximos pasos:")
    print(f"   1. cd {BASE}")
    print("   2. Configura tus variables de entorno (JWT_SECRET, BACKEND_URL, REDIS_*)")
    print("   3. mvn clean install")
    print("   4. mvn spring-boot:run")
    print("   5. Swagger: http://localhost:8081/swagger-ui.html")
    print("\\n🎯 Tests: mvn test && mvn jacoco:report\\n")


if __name__ == "__main__":
    create_project()
