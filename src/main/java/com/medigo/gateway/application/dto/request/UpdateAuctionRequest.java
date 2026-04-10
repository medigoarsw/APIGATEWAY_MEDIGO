package com.medigo.gateway.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de actualización de subasta.
 * Per spec: basePrice (> 0), startTime, endTime requeridos
 */
@Data
public class UpdateAuctionRequest {

    @NotNull(message = "basePrice es requerido")
    @Positive(message = "basePrice debe ser mayor a 0")
    private BigDecimal basePrice;

    @NotNull(message = "startTime es requerido")
    private LocalDateTime startTime;

    @NotNull(message = "endTime es requerido")
    private LocalDateTime endTime;
}
