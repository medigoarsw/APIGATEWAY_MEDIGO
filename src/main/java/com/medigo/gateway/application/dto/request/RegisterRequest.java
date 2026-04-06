package com.medigo.gateway.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @Pattern(
            regexp = "^$|^\\+\\d{1,3}-\\d{3}-\\d{7}$",
            message = "Phone debe cumplir formato +57-322-5555555"
    )
    private String phone;

    @NotBlank(message = "Role es requerido")
    @Pattern(
            regexp = "^(AFFILIATE|DELIVERY)$",
            message = "Role solo permite AFFILIATE o DELIVERY en self-register"
    )
    private String role; // AFFILIATE o DELIVERY
}
