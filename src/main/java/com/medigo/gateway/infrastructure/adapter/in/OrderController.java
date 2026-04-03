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
 * Controller de órdenes: manejo de carrito y confirmación de pedidos.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Gestión de órdenes y carrito")
@SecurityRequirement(name = "BearerAuth")
public class OrderController {

    private final ForwardingUseCase forwardingUseCase;

    // ===== CARRITO =====

    @PostMapping("/cart/add")
    @Operation(summary = "Agregar medicamento al carrito (HU-04)")
    public ResponseEntity<Object> addToCart(
            @RequestBody Object body, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/orders/cart/add", req, body);
    }

    @GetMapping("/cart")
    @Operation(summary = "Obtener carrito actual (HU-04)")
    public ResponseEntity<Object> getCart(HttpServletRequest req) {
        return forwardingUseCase.forward("/api/orders/cart", req, null);
    }

    @DeleteMapping("/cart/{cartId}/{medicationId}")
    @Operation(summary = "Eliminar medicamento del carrito (HU-04)")
    public ResponseEntity<Object> removeFromCart(
            @PathVariable Long cartId,
            @PathVariable Long medicationId,
            HttpServletRequest req) {
        return forwardingUseCase.forward(
                "/api/orders/cart/" + cartId + "/" + medicationId, req, null);
    }

    // ===== ÓRDENES =====

    @PostMapping("/confirm")
    @Operation(summary = "Confirmar orden (HU-05)")
    public ResponseEntity<Object> confirmOrder(
            @RequestBody Object body, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/orders/confirm", req, body);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Obtener detalle de orden (HU-05)")
    public ResponseEntity<Object> getOrder(@PathVariable Long orderId, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/orders/" + orderId, req, null);
    }

    @GetMapping("/affiliate/{affiliateId}")
    @Operation(summary = "Órdenes del cliente (HU-05)")
    public ResponseEntity<Object> byAffiliate(
            @PathVariable Long affiliateId, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/orders/affiliate/" + affiliateId, req, null);
    }
}
