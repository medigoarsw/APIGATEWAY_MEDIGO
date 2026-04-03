package com.medigo.gateway.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de solicitud de registro.
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "Name es requerido")
    @Size(min = 3, max = 100, message = "Name debe tener entre 3 y 100 caracteres")
    private String name;

    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe ser válido")
    private String email;

    @NotBlank(message = "Password es requerido")
    private String password;

    @NotBlank(message = "Role es requerido")
    private String role; // AFFILIATE o DELIVERY
}
