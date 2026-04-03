package com.medigo.gateway.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para confirmar orden (dirección de entrega).
 * Per spec: street, streetNumber, city, commune (requeridos)
 *           latitude, longitude (opcionales)
 */
@Data
public class ConfirmOrderRequest {

    @NotBlank(message = "street es requerida")
    private String street;

    @NotBlank(message = "streetNumber es requerido")
    private String streetNumber;

    @NotBlank(message = "city es requerida")
    private String city;

    @NotBlank(message = "commune es requerida")
    private String commune;

    private Double latitude;

    private Double longitude;
}
