package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.application.dto.request.LoginRequest;
import com.medigo.gateway.application.dto.response.GatewayResponse;
import com.medigo.gateway.application.dto.response.LoginResponse;
import com.medigo.gateway.domain.port.in.AuthUseCase;
import com.medigo.gateway.domain.port.in.ForwardingUseCase;
import com.medigo.gateway.infrastructure.common.TraceIdHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de autenticación: login, registro y perfil de usuario.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autenticación JWT y perfil de usuario")
public class AuthController {

    private final AuthUseCase authUseCase;
    private final ForwardingUseCase forwardingUseCase;

    // ===== PÚBLICOS (sin JWT) =====

    @PostMapping("/login")
    @Operation(summary = "Login - genera JWT (Público - HU-01)")
    public ResponseEntity<GatewayResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authUseCase.login(request);
        return ResponseEntity.ok(GatewayResponse.ok(response, TraceIdHolder.get()));
    }

    @PostMapping("/register")
    @Operation(summary = "Registro de usuario (Público)")
    public ResponseEntity<Object> register(@RequestBody Object body, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auth/register", req, body);
    }

    // ===== PROTEGIDOS (JWT requerido) =====

    @GetMapping("/me")
    @Operation(summary = "Obtener perfil del usuario actual (HU-02)")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<Object> getCurrentUser(HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auth/me", req, null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<Object> getUserById(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auth/" + id, req, null);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Obtener usuario por email")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<Object> getUserByEmail(@PathVariable String email, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auth/email/" + email, req, null);
    }
}
