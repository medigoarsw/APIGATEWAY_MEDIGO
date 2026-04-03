package com.medigo.gateway.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO para crear un nuevo medicamento.
 * Per spec: name, unit, price (>0), branchId (>0), initialStock (>0)
 */
@Data
public class CreateMedicationRequest {

    @NotBlank(message = "name es requerido")
    private String name;

    private String description;

    @NotBlank(message = "unit es requerido")
    private String unit;

    @NotNull(message = "price es requerido")
    @Positive(message = "price debe ser mayor a 0")
    private Double price;

    @NotNull(message = "branchId es requerido")
    @Min(value = 1, message = "branchId debe ser mayor a 0")
    private Long branchId;

    @NotNull(message = "initialStock es requerido")
    @Min(value = 1, message = "initialStock debe ser mayor a 0")
    private Integer initialStock;
}
