package com.medigo.gateway.application.dto.response;

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
