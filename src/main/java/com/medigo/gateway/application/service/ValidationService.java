package com.medigo.gateway.application.service;

import com.medigo.gateway.application.dto.request.CreateAuctionRequest;
import com.medigo.gateway.infrastructure.exception.GatewayValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Servicio de validaciones de negocio adicionales (más allá de Bean Validation).
 */
@Slf4j
@Service
public class ValidationService {

    /**
     * Valida reglas de negocio para crear subasta.
     */
    public void validateCreateAuction(CreateAuctionRequest req) {
        if (req.getEndTime().isBefore(req.getStartTime()) ||
            req.getEndTime().isEqual(req.getStartTime())) {
            throw new GatewayValidationException("endTime debe ser posterior a startTime");
        }
        log.debug("CreateAuctionRequest validado correctamente");
    }
}
