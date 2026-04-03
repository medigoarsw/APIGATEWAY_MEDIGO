package com.medigo.gateway.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO de creación de orden.
 * Per spec: affiliateId, branchId, addressLat (opt), addressLng (opt), notes (opt)
 */
@Data
public class CreateOrderRequest {

    @NotNull(message = "affiliateId es requerido")
    @Min(value = 1, message = "affiliateId debe ser mayor a 0")
    private Long affiliateId;

    @NotNull(message = "branchId es requerido")
    @Min(value = 1, message = "branchId debe ser mayor a 0")
    private Long branchId;

    private Double addressLat;

    private Double addressLng;

    private String notes;
}
