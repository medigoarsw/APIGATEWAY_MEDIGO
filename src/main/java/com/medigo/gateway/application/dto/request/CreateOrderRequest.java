package com.medigo.gateway.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

/**
 * DTO de creación de orden.
 */
@Data
public class CreateOrderRequest {

    @NotNull private Long affiliateId;
    @NotNull private Long branchId;

    @DecimalMin("-90.0") @DecimalMax("90.0")
    private Double lat;

    @DecimalMin("-180.0") @DecimalMax("180.0")
    private Double lng;

    @NotEmpty(message = "items no puede estar vacío")
    private List<Object> items;
}
