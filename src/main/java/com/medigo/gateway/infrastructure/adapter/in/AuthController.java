package com.medigo.gateway.infrastructure.adapter.in;

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
