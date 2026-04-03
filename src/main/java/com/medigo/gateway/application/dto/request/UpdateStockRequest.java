package com.medigo.gateway.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO para actualizar stock de medicamento en sucursal.
 * Per spec: medicationId (requerido), quantity (>= 0)
 */
@Data
public class UpdateStockRequest {

    @NotNull(message = "medicationId es requerido")
    @Min(value = 1, message = "medicationId debe ser mayor a 0")
    private Long medicationId;

    @NotNull(message = "quantity es requerida")
    @Min(value = 0, message = "quantity debe ser >= 0")
    private Integer quantity;
}
