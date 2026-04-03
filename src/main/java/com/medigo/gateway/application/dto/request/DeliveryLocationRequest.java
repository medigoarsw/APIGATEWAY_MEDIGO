package com.medigo.gateway.application.dto.request;

import lombok.Data;

/**
 * DTO para actualizar ubicación de entrega.
 * Nota: Especificación en backend aún define como Object.
 * Campos sugeridos para ubicación GPS y dirección.
 */
@Data
public class DeliveryLocationRequest {

    private Double latitude;
    private Double longitude;
    private String address;
    private String notes;
}
