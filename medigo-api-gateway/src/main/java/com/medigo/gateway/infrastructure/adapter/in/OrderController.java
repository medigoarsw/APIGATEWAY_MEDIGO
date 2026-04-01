package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.application.dto.request.CreateOrderRequest;
import com.medigo.gateway.domain.port.in.ForwardingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de órdenes.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Gestión de órdenes")
@SecurityRequirement(name = "BearerAuth")
public class OrderController {

    private final ForwardingUseCase forwardingUseCase;

    @PostMapping
    @Operation(summary = "Crear orden")
    public ResponseEntity<Object> create(
            @Valid @RequestBody CreateOrderRequest body, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/orders", req, body);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener orden por ID")
    public ResponseEntity<Object> getById(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/orders/" + id, req, null);
    }

    @GetMapping("/affiliate/{affiliateId}")
    @Operation(summary = "Órdenes por afiliado")
    public ResponseEntity<Object> byAffiliate(
            @PathVariable Long affiliateId, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/orders/affiliate/" + affiliateId, req, null);
    }
}
