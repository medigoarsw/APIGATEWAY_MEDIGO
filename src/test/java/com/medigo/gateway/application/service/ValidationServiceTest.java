package com.medigo.gateway.application.service;

import com.medigo.gateway.application.dto.request.CreateAuctionRequest;
import com.medigo.gateway.infrastructure.exception.GatewayValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class ValidationServiceTest {

    private final ValidationService service = new ValidationService();

    @Test
    void testValidCreateAuctionPasses() {
        CreateAuctionRequest req = new CreateAuctionRequest();
        req.setMedicationId(1L);
        req.setBranchId(1L);
        req.setBasePrice(BigDecimal.TEN);
        req.setStartTime(LocalDateTime.now());
        req.setEndTime(LocalDateTime.now().plusHours(2));

        assertThatCode(() -> service.validateCreateAuction(req)).doesNotThrowAnyException();
    }

    @Test
    void testEndTimeBeforeStartTimeThrows() {
        CreateAuctionRequest req = new CreateAuctionRequest();
        req.setMedicationId(1L);
        req.setBranchId(1L);
        req.setBasePrice(BigDecimal.TEN);
        req.setStartTime(LocalDateTime.now().plusHours(2));
        req.setEndTime(LocalDateTime.now());

        assertThatThrownBy(() -> service.validateCreateAuction(req))
                .isInstanceOf(GatewayValidationException.class)
                .hasMessageContaining("endTime");
    }
}
