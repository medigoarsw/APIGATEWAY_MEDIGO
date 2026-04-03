package com.medigo.gateway.application.service;

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
import static org.mockito.Mockito.lenient;
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

        lenient().when(backendClient.send(eq("/api/auth/login"), eq(HttpMethod.POST), any(), any()))
                .thenReturn(ResponseEntity.ok(backendBody));

        UserClaims claims = UserClaims.builder()
                .userId("1").username("testuser")
                .email("test@test.com").role("USUARIO").build();

                lenient().when(jwtPort.generateToken(any())).thenReturn("mocked.jwt.token");
    }

    @Test
    void testLoginReturnsJwtToken() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@test.com");
        req.setPassword("password123");

        LoginResponse response = service.login(req);

        assertThat(response.getJwtToken()).isEqualTo("mocked.jwt.token");
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getRole()).isEqualTo("USUARIO");
    }

    @Test
    void testLoginWithWrappedBackendPayload() {
        Map<String, Object> wrappedBackendBody = Map.of(
                "success", true,
                "data", Map.of(
                        "id", 2,
                        "username", "wrappedUser",
                        "email", "wrapped@test.com",
                        "role", "ADMIN"
                )
        );

        when(backendClient.send(eq("/api/auth/login"), eq(HttpMethod.POST), any(), any()))
                .thenReturn(ResponseEntity.ok(wrappedBackendBody));

        LoginRequest req = new LoginRequest();
        req.setEmail("wrapped@test.com");
        req.setPassword("password123");

        LoginResponse response = service.login(req);

        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getUsername()).isEqualTo("wrappedUser");
        assertThat(response.getRole()).isEqualTo("ADMIN");
        assertThat(response.getJwtToken()).isEqualTo("mocked.jwt.token");
    }
}
