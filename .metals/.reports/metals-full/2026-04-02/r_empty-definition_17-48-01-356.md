error id: file:///D:/ander/Documents/SEMESTRE%207/ARSW/PROYECTO%20OFICIAL/APIGATEWAY_MEDIGO/src/main/java/com/medigo/gateway/application/service/AuthGatewayService.java:_empty_/JwtPort#generateToken#
file:///D:/ander/Documents/SEMESTRE%207/ARSW/PROYECTO%20OFICIAL/APIGATEWAY_MEDIGO/src/main/java/com/medigo/gateway/application/service/AuthGatewayService.java
empty definition using pc, found symbol in pc: _empty_/JwtPort#generateToken#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 3388
uri: file:///D:/ander/Documents/SEMESTRE%207/ARSW/PROYECTO%20OFICIAL/APIGATEWAY_MEDIGO/src/main/java/com/medigo/gateway/application/service/AuthGatewayService.java
text:
```scala
package com.medigo.gateway.application.service;

import com.medigo.gateway.application.dto.request.LoginRequest;
import com.medigo.gateway.application.dto.response.LoginResponse;
import com.medigo.gateway.domain.model.UserClaims;
import com.medigo.gateway.domain.port.in.AuthUseCase;
import com.medigo.gateway.domain.port.out.BackendClient;
import com.medigo.gateway.domain.port.out.JwtPort;
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
        log.debug("Forwarding login request for user: {}", request.getUsername());

        ResponseEntity<Object> backendResponse = backendClient.send(
                "/api/auth/login", HttpMethod.POST, Map.of(), request
        );

        if (backendResponse.getStatusCode() == HttpStatus.UNAUTHORIZED
 backendResponse.getStatusCode() == HttpStatus.FORBIDDEN) {
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

        UserClaims claims = UserClaims.builder()
                .userId(id)
                .username(username)
                .email(email == null ? "" : email)
                .role(role == null || role.isBlank() ? "USUARIO" : role)
                .build();

        String jwt = jwtPort.@@generateToken(claims);

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
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/JwtPort#generateToken#