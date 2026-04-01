package com.medigo.gateway.application.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de actualización de subasta.
 */
@Data
public class UpdateAuctionRequest {

    @Positive(message = "basePrice debe ser mayor a 0")
    private BigDecimal basePrice;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
