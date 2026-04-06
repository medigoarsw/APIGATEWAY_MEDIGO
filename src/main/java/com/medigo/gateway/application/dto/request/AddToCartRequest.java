package com.medigo.gateway.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para agregar medicamento al carrito.
 * Per spec: affiliateId, branchId, medicationId, quantity (1-100)
 */
@Data
public class AddToCartRequest {

    @NotNull(message = "affiliateId es requerido")
    @Min(value = 1, message = "affiliateId debe ser mayor a 0")
    private Long affiliateId;

    @NotNull(message = "branchId es requerido")
    @Min(value = 1, message = "branchId debe ser mayor a 0")
    private Long branchId;

    @NotNull(message = "medicationId es requerido")
    @Min(value = 1, message = "medicationId debe ser mayor a 0")
    private Long medicationId;

    @NotNull(message = "quantity es requerida")
    @Min(value = 1, message = "quantity debe ser al menos 1")
    @Max(value = 100, message = "quantity debe ser m\u00e1ximo 100")
    private Long quantity;
}
