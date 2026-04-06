package com.medigo.gateway.application.service;

import com.medigo.gateway.application.dto.request.LoginRequest;
import com.medigo.gateway.application.dto.request.RegisterRequest;
import com.medigo.gateway.application.dto.response.LoginResponse;
import com.medigo.gateway.application.dto.response.RegisterResponse;
import com.medigo.gateway.application.dto.response.UserResponseDto;
import com.medigo.gateway.domain.model.UserClaims;
import com.medigo.gateway.domain.port.in.AuthUseCase;
import com.medigo.gateway.domain.port.out.BackendClient;
import com.medigo.gateway.domain.port.out.JwtPort;
import com.medigo.gateway.infrastructure.common.RoleMapper;
import com.medigo.gateway.infrastructure.exception.GatewayValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
        log.debug("Forwarding login request for user: {}", request.getEmail());

        ResponseEntity<Object> backendResponse = backendClient.send(
                "/api/auth/login", HttpMethod.POST, Map.of(), request
        );

        if (backendResponse.getStatusCode() == HttpStatus.UNAUTHORIZED
                || backendResponse.getStatusCode() == HttpStatus.FORBIDDEN) {
            throw new GatewayValidationException("Credenciales inválidas");
        }
        if (backendResponse.getStatusCode().isError()) {
            log.error("Backend respondió con error {}: {}", backendResponse.getStatusCode(), backendResponse.getBody());
            throw new IllegalStateException("Error en el backend al autenticar: " + backendResponse.getStatusCode());
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) backendResponse.getBody();

        if (body == null) {
            throw new IllegalStateException("Backend returned empty body on login");
        }

        Map<String, Object> payload = resolvePayload(body);
        String userId = readAsString(payload, "user_id", "id", "userId");
        String username = readAsString(payload, "username", "userName");
        String email = readAsString(payload, "email");
        String role = readAsString(payload, "role");

        if (userId == null || userId.isBlank()) {
            throw new IllegalStateException("Backend login response missing user id");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalStateException("Backend login response missing username");
        }

        long id;
        try {
            id = Long.parseLong(userId);
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Backend login response contains invalid user id: " + userId, ex);
        }

        // Log para debugging de roles
        log.info("Backend returned user: userId='{}', username='{}', email='{}', role='{}'", 
                 id, username, email, role);

        String canonicalRole = RoleMapper.toCanonical(role);
        log.info("Role mapping: backend_role='{}' -> canonical_role='{}'", role, canonicalRole);
        
        UserClaims claims = UserClaims.builder()
                .userId(String.valueOf(id))
                .username(username)
                .email(email == null ? "" : email)
                .role(canonicalRole)
                .build();

        String jwt = jwtPort.generateToken(claims);

        return LoginResponse.builder()
                .id(id)
                .username(claims.getUsername())
                .email(claims.getEmail())
                .role(claims.getRole())
                .jwtToken(jwt)
                .build();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> resolvePayload(Map<String, Object> body) {
        Object data = body.get("data");
        if (data instanceof Map<?, ?> dataMap) {
            return (Map<String, Object>) dataMap;
        }
        return new HashMap<>(body);
    }

    private String readAsString(Map<String, Object> source, String... keys) {
        for (String key : keys) {
            Object value = source.get(key);
            if (value != null) {
                return String.valueOf(value);
            }
        }
        return null;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        log.debug("Forwarding register request for user: {}", request.getEmail());

        ResponseEntity<Object> backendResponse = backendClient.send(
                "/api/auth/register", HttpMethod.POST, Map.of(), request
        );

        if (backendResponse.getStatusCode().isError()) {
            log.error("Backend error on register: {}", backendResponse.getStatusCode());
            throw new GatewayValidationException("Error en el backend al registrar usuario");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) backendResponse.getBody();
        if (body == null) {
            throw new IllegalStateException("Backend returned empty body on register");
        }

        Map<String, Object> payload = resolvePayload(body);
        String id = readAsString(payload, "id");
        String name = readAsString(payload, "name");
        String email = readAsString(payload, "email");
        String role = readAsString(payload, "role");
        
        String canonicalRole = RoleMapper.toCanonical(role);

        return RegisterResponse.builder()
                .id(id != null ? Long.valueOf(id) : null)
                .name(name)
                .email(email)
                .role(canonicalRole)
                .message("Usuario registrado exitosamente")
                .build();
    }

    @Override
    public UserResponseDto getMe(Long userId) {
        log.debug("Getting user info for userId: {}", userId);

        ResponseEntity<Object> backendResponse = backendClient.send(
                "/api/auth/me?user_id=" + userId, HttpMethod.GET, Map.of(), null
        );

        if (backendResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new GatewayValidationException("Usuario no encontrado");
        }
        if (backendResponse.getStatusCode().isError()) {
            throw new IllegalStateException("Error en el backend al obtener usuario");
        }

        return mapToUserResponseDto(backendResponse.getBody());
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        log.debug("Getting user by id: {}", id);

        ResponseEntity<Object> backendResponse = backendClient.send(
                "/api/auth/" + id, HttpMethod.GET, Map.of(), null
        );

        if (backendResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new GatewayValidationException("Usuario no encontrado");
        }
        if (backendResponse.getStatusCode().isError()) {
            throw new IllegalStateException("Error en el backend al obtener usuario");
        }

        return mapToUserResponseDto(backendResponse.getBody());
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        log.debug("Getting user by email: {}", email);

        ResponseEntity<Object> backendResponse = backendClient.send(
                "/api/auth/email/" + email, HttpMethod.GET, Map.of(), null
        );

        if (backendResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new GatewayValidationException("Usuario no encontrado");
        }
        if (backendResponse.getStatusCode().isError()) {
            throw new IllegalStateException("Error en el backend al obtener usuario");
        }

        return mapToUserResponseDto(backendResponse.getBody());
    }

    @SuppressWarnings("unchecked")
    private UserResponseDto mapToUserResponseDto(Object response) {
        Map<String, Object> body = (Map<String, Object>) response;
        if (body == null) {
            throw new IllegalStateException("Backend returned empty body");
        }

        Map<String, Object> payload = resolvePayload(body);
        String userId = readAsString(payload, "user_id", "id", "userId");
        String username = readAsString(payload, "username", "userName");
        String email = readAsString(payload, "email");
        String role = readAsString(payload, "role");
        Boolean active = Boolean.TRUE; // default

        Object activeObj = payload.get("active");
        if (activeObj instanceof Boolean) {
            active = (Boolean) activeObj;
        }

        return UserResponseDto.builder()
                .user_id(userId != null ? Long.valueOf(userId) : null)
                .username(username)
                .email(email)
                .role(role)
                .active(active)
                .build();
    }
}
