package com.medigo.gateway.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de creación de subasta.
 */
@Data
public class CreateAuctionRequest {

    @NotNull(message = "medicationId es requerido")
    private Long medicationId;

    @NotNull(message = "branchId es requerido")
    private Long branchId;

    @NotNull @Positive(message = "basePrice debe ser mayor a 0")
    private BigDecimal basePrice;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;
}
