package com.medigo.gateway.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de solicitud de login.
 * Per spec: email (not username), password
 */
@Data
public class LoginRequest {

    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe ser válido")
    private String email;

    @NotBlank(message = "Password es requerido")
    @Size(min = 1, max = 255, message = "Password debe tener entre 1 y 255 caracteres")
    private String password;
}
