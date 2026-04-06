package com.medigo.gateway.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO para asignar entrega a repartidor.
 * Nota: Especificación en backend aún define como Object.
 * Campos sugeridos para asignar orden a delivery person.
 */
@Data
public class AssignDeliveryRequest {

    @NotNull(message = "deliveryPersonId es requerido")
    @Min(value = 1, message = "deliveryPersonId debe ser mayor a 0")
    private Long deliveryPersonId;

    @NotNull(message = "orderId es requerido")
    @Min(value = 1, message = "orderId debe ser mayor a 0")
    private Long orderId;
}
