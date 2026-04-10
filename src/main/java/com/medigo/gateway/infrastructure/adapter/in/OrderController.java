package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.application.dto.request.AddToCartRequest;
import com.medigo.gateway.application.dto.request.ConfirmOrderRequest;
import com.medigo.gateway.application.dto.request.CreateOrderRequest;
import com.medigo.gateway.domain.port.in.ForwardingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Gestión de pedidos")
public class OrderController {

    private final ForwardingUseCase forwardingUseCase;

    @PostMapping("/cart/add")
    @PreAuthorize("hasRole('AFFILIATE')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Agregar al carrito (AFFILIATE ONLY)")
    public ResponseEntity<Object> addToCart(@Valid @RequestBody AddToCartRequest body, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/orders/cart/add", req, body);
    }

    @GetMapping("/cart")
    @PreAuthorize("hasRole('AFFILIATE')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Ver carrito (AFFILIATE ONLY)")
    public ResponseEntity<Object> getCart(
            @RequestParam Long affiliateId,
            @RequestParam Long branchId,
            HttpServletRequest req) {
        return forwardingUseCase.forward("/api/orders/cart?affiliateId=" + affiliateId + "&branchId=" + branchId, req, null);
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY', 'REPARTIDOR', 'DELIVERY_PERSON')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Obtener órdenes disponibles para repartidores")
    public ResponseEntity<Object> getAvailable(HttpServletRequest req) {
        return forwardingUseCase.forward("/api/orders/available", req, null);
    }

    @PostMapping("/{branchId}/confirm")
    @PreAuthorize("hasRole('AFFILIATE')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Confirmar pedido (AFFILIATE ONLY)")
    public ResponseEntity<Object> confirm(
            @PathVariable Long branchId,
            @RequestParam Long affiliateId,
            @Valid @RequestBody ConfirmOrderRequest body,
            HttpServletRequest req) {
        return forwardingUseCase.forward("/api/orders/" + branchId + "/confirm?affiliateId=" + affiliateId, req, body);
    }

    @PostMapping
    @PreAuthorize("hasRole('AFFILIATE')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Crear nuevo carrito (AFFILIATE ONLY)")
    public ResponseEntity<Object> create(@Valid @RequestBody CreateOrderRequest body, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/orders", req, body);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('AFFILIATE')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Mis pedidos (AFFILIATE ONLY)")
    public ResponseEntity<Object> myOrders(HttpServletRequest req) {
        return forwardingUseCase.forward("/api/orders/me", req, null);
    }
}
