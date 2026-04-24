package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.application.dto.request.CreateOrderRequest;
import com.medigo.gateway.application.dto.request.AddToCartRequest;
import com.medigo.gateway.application.dto.request.ConfirmOrderRequest;
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

/**
 * Controller de órdenes y carrito.
 * Todos los endpoints requieren AFFILIATE.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Gestión de órdenes y carrito")
public class OrderController {

    private final ForwardingUseCase forwardingUseCase;

    // ========== CARRITO (AFFILIATE) ==========

    @PostMapping("/cart/add")
    @PreAuthorize("hasRole('AFFILIATE')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Agregar medicamento al carrito (AFFILIATE ONLY)")
    public ResponseEntity<Object> addToCart(
            @Valid @RequestBody AddToCartRequest body, HttpServletRequest req) {
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
        return forwardingUseCase.forward(
                "/api/orders/cart?affiliateId=" + affiliateId + "&branchId=" + branchId,
                req, null);
    }

    @PostMapping
    @PreAuthorize("hasRole('AFFILIATE')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Crear orden (AFFILIATE ONLY)")
    public ResponseEntity<Object> create(
            @Valid @RequestBody CreateOrderRequest body, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/orders", req, body);
    }

    @PostMapping("/{branchId}/confirm")
    @PreAuthorize("hasRole('AFFILIATE')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Confirmar orden (AFFILIATE ONLY)")
    public ResponseEntity<Object> confirm(
            @PathVariable Long branchId,
            @RequestParam Long affiliateId,
            @Valid @RequestBody ConfirmOrderRequest body,
            HttpServletRequest req) {
        return forwardingUseCase.forward(
                "/api/orders/" + branchId + "/confirm?affiliateId=" + affiliateId,
                req, body);
    }

    // ========== DELIVERY ==========

    @GetMapping
    @PreAuthorize("hasAnyRole('DELIVERY', 'ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Listar pedidos por estado (DELIVERY/ADMIN) — e.g. ?status=CONFIRMED")
    public ResponseEntity<Object> getByStatus(
            @RequestParam(required = false) String status, HttpServletRequest req) {
        String path = status != null ? "/api/orders?status=" + status : "/api/orders";
        return forwardingUseCase.forward(path, req, null);
    }

    // ========== LEGACY (No en especificación, mantener pero deprecar) ==========

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('AFFILIATE')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "[DEPRECADO] Obtener orden por ID")
    public ResponseEntity<Object> getById(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/orders/" + id, req, null);
    }

    @GetMapping("/affiliate/{affiliateId}")
    @PreAuthorize("hasRole('AFFILIATE')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "[DEPRECADO] Órdenes por afiliado")
    public ResponseEntity<Object> byAffiliate(
            @PathVariable Long affiliateId, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/orders/affiliate/" + affiliateId, req, null);
    }
}
