package com.medigo.gateway.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO de oferta en subasta.
 */
@Data
public class PlaceBidRequest {

    @NotNull @Positive(message = "amount debe ser mayor a 0")
    private BigDecimal amount;

    @NotBlank(message = "userName es requerido")
    private String userName;

    @NotNull(message = "userId es requerido")
    private Long userId;
}
