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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
                || backendResponse.getStatusCode() == HttpStatus.FORBIDDEN
                || backendResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new GatewayValidationException("Correo o contraseña incorrectos.", HttpStatus.UNAUTHORIZED);
        }
        if (backendResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
            throw new GatewayValidationException("Verifica el correo y la contraseña e intenta nuevamente.", HttpStatus.BAD_REQUEST);
        }
        if (backendResponse.getStatusCode().isError()) {
            log.error("Backend respondió con error {}: {}", backendResponse.getStatusCode(), backendResponse.getBody());
            throw new GatewayValidationException(
                    "No fue posible iniciar sesión en este momento. Intenta nuevamente.",
                    HttpStatus.BAD_GATEWAY
            );
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) backendResponse.getBody();

        if (body == null) {
            throw new GatewayValidationException(
                    "No fue posible iniciar sesión en este momento. Intenta nuevamente.",
                    HttpStatus.BAD_GATEWAY
            );
        }

        Map<String, Object> payload = resolvePayload(body);
        String userId = readAsString(payload, "user_id", "id", "userId");
        String username = readAsString(payload, "username", "userName");
        String email = readAsString(payload, "email");
        String role = readAsString(payload, "role");

        if (userId == null || userId.isBlank()) {
            throw new GatewayValidationException(
                    "No fue posible iniciar sesión en este momento. Intenta nuevamente.",
                    HttpStatus.BAD_GATEWAY
            );
        }
        if (username == null || username.isBlank()) {
            throw new GatewayValidationException(
                    "No fue posible iniciar sesión en este momento. Intenta nuevamente.",
                    HttpStatus.BAD_GATEWAY
            );
        }

        long id;
        try {
            id = Long.parseLong(userId);
        } catch (NumberFormatException ex) {
            throw new GatewayValidationException(
                    "No fue posible iniciar sesión en este momento. Intenta nuevamente.",
                    HttpStatus.BAD_GATEWAY
            );
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

    private LocalDateTime readAsLocalDateTime(Map<String, Object> source, String... keys) {
        String value = readAsString(source, keys);
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim().replace(' ', 'T');
        try {
            return LocalDateTime.parse(normalized, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception ex) {
            log.warn("No se pudo parsear datetime de backend: {}", value);
            return null;
        }
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        log.debug("Forwarding register request for user: {}", request.getEmail());

        ResponseEntity<Object> backendResponse = backendClient.send(
                "/api/auth/register", HttpMethod.POST, Map.of(), request
        );

        if (backendResponse.getStatusCode().isError()) {
            HttpStatus backendStatus = HttpStatus.valueOf(backendResponse.getStatusCode().value());
            String backendMessage = extractBackendErrorMessage(backendResponse.getBody());
            String resolvedMessage = resolveRegisterErrorMessage(backendMessage, backendStatus);
            HttpStatus resolvedStatus = resolveRegisterErrorStatus(backendStatus, resolvedMessage);

            log.warn("Register rejected by backend. status={}, message={}", backendStatus, resolvedMessage);
            throw new GatewayValidationException(resolvedMessage, resolvedStatus);
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
        String phone = readAsString(payload, "phone");
        String role = readAsString(payload, "role");
        LocalDateTime createdAt = readAsLocalDateTime(payload, "createdAt");
        LocalDateTime updatedAt = readAsLocalDateTime(payload, "updatedAt");
        
        String canonicalRole = RoleMapper.toCanonical(role);

        return RegisterResponse.builder()
                .id(id != null ? Long.valueOf(id) : null)
                .name(name)
                .email(email)
                .phone(phone)
                .role(canonicalRole)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .message("Usuario registrado exitosamente")
                .build();
    }

    @SuppressWarnings("unchecked")
    private String extractBackendErrorMessage(Object body) {
        if (!(body instanceof Map<?, ?> rawMap)) {
            return null;
        }

        Map<String, Object> map = (Map<String, Object>) rawMap;
        Object directMessage = map.get("message");
        if (directMessage != null && !String.valueOf(directMessage).isBlank()) {
            return String.valueOf(directMessage);
        }

        Object error = map.get("error");
        if (error != null && !String.valueOf(error).isBlank()) {
            return String.valueOf(error);
        }

        Object details = map.get("details");
        if (details != null && !String.valueOf(details).isBlank()) {
            return String.valueOf(details);
        }

        Object data = map.get("data");
        if (data instanceof Map<?, ?> dataMap) {
            Object nestedMessage = dataMap.get("message");
            if (nestedMessage != null && !String.valueOf(nestedMessage).isBlank()) {
                return String.valueOf(nestedMessage);
            }
        }

        return null;
    }

    private String resolveRegisterErrorMessage(String backendMessage, HttpStatus backendStatus) {
        String normalized = String.valueOf(backendMessage).toLowerCase();

        if (normalized.contains("contraseña") || normalized.contains("password") || normalized.contains("weak")) {
            return "La contraseña no cumple los requisitos de seguridad.";
        }
        if (normalized.contains("email") && (normalized.contains("válido") || normalized.contains("valido") || normalized.contains("formato"))) {
            return "El correo no tiene un formato válido.";
        }
        if (normalized.contains("phone") || normalized.contains("teléfono") || normalized.contains("telefono")) {
            return "El teléfono no es válido. Usa el formato +57-322-5555555.";
        }
        if (normalized.contains("rol") || normalized.contains("role") || normalized.contains("affiliate") || normalized.contains("delivery")) {
            return "El rol enviado no es válido. Solo se permiten AFFILIATE o DELIVERY.";
        }
        if (normalized.contains("ya se encuentra registrado")
                || normalized.contains("already exists")
                || normalized.contains("duplic")
                || normalized.contains("ya existe")) {
            return "No fue posible crear la cuenta con los datos ingresados.";
        }

        return switch (backendStatus) {
            case CONFLICT -> "No fue posible crear la cuenta con los datos ingresados.";
            case UNAUTHORIZED, FORBIDDEN -> "No autorizado para crear la cuenta con los datos enviados.";
            case BAD_REQUEST -> "Datos inválidos para crear la cuenta. Verifique email, contraseña, rol y teléfono.";
            case SERVICE_UNAVAILABLE, BAD_GATEWAY, GATEWAY_TIMEOUT -> "El backend no está disponible temporalmente. Intente de nuevo.";
            default -> "No fue posible crear la cuenta en este momento. Intenta nuevamente.";
        };
    }

    private HttpStatus resolveRegisterErrorStatus(HttpStatus backendStatus, String message) {
        if (backendStatus == HttpStatus.BAD_REQUEST && looksLikeConflict(message)) {
            return HttpStatus.CONFLICT;
        }

        if (backendStatus.is4xxClientError() || backendStatus == HttpStatus.CONFLICT) {
            return backendStatus;
        }

        if (backendStatus == HttpStatus.SERVICE_UNAVAILABLE
                || backendStatus == HttpStatus.BAD_GATEWAY
                || backendStatus == HttpStatus.GATEWAY_TIMEOUT) {
            return backendStatus;
        }

        return HttpStatus.BAD_GATEWAY;
    }

    private boolean looksLikeConflict(String message) {
        String normalized = String.valueOf(message).toLowerCase();
        return normalized.contains("ya se encuentra registrado")
                || normalized.contains("already exists")
                || normalized.contains("duplic")
                || normalized.contains("ya existe");
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
