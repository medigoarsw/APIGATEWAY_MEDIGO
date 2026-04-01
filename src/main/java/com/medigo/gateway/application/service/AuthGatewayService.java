package com.medigo.gateway.application.service;

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
