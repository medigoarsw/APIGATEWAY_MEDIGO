package com.medigo.gateway.application.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(
            description = "Fecha/hora local de inicio (sin zona, no usar sufijo Z)",
            type = "string",
            example = "2026-04-09T15:23:00"
        )
    private LocalDateTime startTime;

    @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(
            description = "Fecha/hora local de fin (sin zona, no usar sufijo Z)",
            type = "string",
            example = "2026-04-09T15:33:00"
        )
    private LocalDateTime endTime;

    // FIXED_TIME o INACTIVITY
    private String closureType;

    private BigDecimal maxPrice;

    private Integer inactivityMinutes;
}
