package com.medigo.gateway.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO de oferta en subasta.
 * Per spec: userId, userName, amount (> 0)
 */
@Data
public class PlaceBidRequest {

    @NotNull(message = "userId es requerido")
    private Long userId;

    @NotBlank(message = "userName es requerido")
    private String userName;

    @NotNull(message = "amount es requerido")
    @Positive(message = "amount debe ser mayor a 0")
    private Double amount;
}
