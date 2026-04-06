package com.medigo.gateway.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO de respuesta de registro.
 */
@Data
@Builder
public class RegisterResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
    private String message;
}
