package com.medigo.gateway.application.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * DTO de respuesta de usuario (info pública del usuario).
 */
@Data
@Builder
public class UserResponseDto {
    private Long user_id;
    private String username;
    private String email;
    private String role;
    private Boolean active;
}
