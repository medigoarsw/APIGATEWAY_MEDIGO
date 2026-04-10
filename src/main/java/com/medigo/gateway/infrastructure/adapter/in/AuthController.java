package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.application.dto.request.LoginRequest;
import com.medigo.gateway.application.dto.request.RegisterRequest;
import com.medigo.gateway.application.dto.response.GatewayResponse;
import com.medigo.gateway.application.dto.response.LoginResponse;
import com.medigo.gateway.application.dto.response.RegisterResponse;
import com.medigo.gateway.application.dto.response.UserResponseDto;
import com.medigo.gateway.domain.model.UserClaims;
import com.medigo.gateway.domain.port.in.AuthUseCase;
import com.medigo.gateway.infrastructure.common.TraceIdHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de autenticación: login, registro, y consulta de usuarios.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autenticación JWT")
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    @Operation(summary = "Login - genera JWT (PUBLIC)")
    public ResponseEntity<GatewayResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authUseCase.login(request);
        return ResponseEntity.ok(GatewayResponse.ok(response, TraceIdHolder.get()));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario (PUBLIC)")
    public ResponseEntity<GatewayResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse response = authUseCase.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(GatewayResponse.ok(response, TraceIdHolder.get()));
    }

    @GetMapping("/me")
    @PreAuthorize("authenticated")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Obtener información del usuario actual (AUTHENTICATED)")
    public ResponseEntity<GatewayResponse<UserResponseDto>> getMe(
            @RequestParam(required = false) Long user_id) {
        
        Long finalUserId = user_id;
        
        // Si no viene user_id, intentar extraerlo del token (SecurityContext)
        if (finalUserId == null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserClaims claims) {
                finalUserId = Long.valueOf(claims.getUserId());
            }
        }
        
        if (finalUserId == null) {
            return ResponseEntity.badRequest().body(GatewayResponse.error("user_id es requerido o no pudo ser extraído del token", TraceIdHolder.get()));
        }

        UserResponseDto response = authUseCase.getMe(finalUserId);
        return ResponseEntity.ok(GatewayResponse.ok(response, TraceIdHolder.get()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Obtener usuario por ID (ADMIN ONLY)")
    public ResponseEntity<GatewayResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
        UserResponseDto response = authUseCase.getUserById(id);
        return ResponseEntity.ok(GatewayResponse.ok(response, TraceIdHolder.get()));
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Obtener usuario por email (ADMIN ONLY)")
    public ResponseEntity<GatewayResponse<UserResponseDto>> getUserByEmail(@PathVariable String email) {
        UserResponseDto response = authUseCase.getUserByEmail(email);
        return ResponseEntity.ok(GatewayResponse.ok(response, TraceIdHolder.get()));
    }
}
