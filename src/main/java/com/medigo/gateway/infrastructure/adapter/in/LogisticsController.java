package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.application.dto.request.DeliveryLocationRequest;
import com.medigo.gateway.application.dto.request.AssignDeliveryRequest;
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
 * Controller de logística y entregas.
 * DELIVERY: actualización de ubicación, completar entrega, listar activas, obtener estado.
 * ADMIN: asignar entregas.
 */
@RestController
@RequestMapping("/api/logistics")
@RequiredArgsConstructor
@Tag(name = "Logistics", description = "Logística de entregas")
public class LogisticsController {

    private final ForwardingUseCase forwardingUseCase;

    // ========== DELIVERY ==========

    @PutMapping("/deliveries/{id}/location")
    @PreAuthorize("hasRole('DELIVERY')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Actualizar ubicación de entrega (DELIVERY ONLY)")
    public ResponseEntity<Object> updateLocation(
            @PathVariable Long id, @Valid @RequestBody DeliveryLocationRequest body, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/deliveries/" + id + "/location", req, body);
    }

    @PutMapping("/deliveries/{id}/complete")
    @PreAuthorize("hasRole('DELIVERY')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Marcar entrega como completada (DELIVERY ONLY)")
    public ResponseEntity<Object> complete(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/deliveries/" + id + "/complete", req, null);
    }

    @GetMapping("/deliveries/active")
    @PreAuthorize("hasRole('DELIVERY')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Entregas activas del repartidor (DELIVERY ONLY)")
    public ResponseEntity<Object> activeDeliveries(
            @RequestParam Long deliveryPersonId, HttpServletRequest req) {
        return forwardingUseCase.forward(
                "/api/logistics/deliveries/active?deliveryPersonId=" + deliveryPersonId,
                req, null);
    }

    @GetMapping("/deliveries/{id}")
    @PreAuthorize("hasRole('DELIVERY')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Estado de entrega específica (DELIVERY ONLY)")
    public ResponseEntity<Object> status(
            @PathVariable Long id,
            @RequestParam Long deliveryPersonId,
            HttpServletRequest req) {
        return forwardingUseCase.forward(
                "/api/logistics/deliveries/" + id + "?deliveryPersonId=" + deliveryPersonId,
                req, null);
    }

    @PutMapping("/deliveries/{id}/pickup")
    @PreAuthorize("hasRole('DELIVERY')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Confirmar recogida en sucursal — IN_ROUTE (DELIVERY ONLY)")
    public ResponseEntity<Object> markPickup(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/deliveries/" + id + "/pickup", req, null);
    }

    // ========== DELIVERY + ADMIN ==========

    @PostMapping("/deliveries/assign")
    @PreAuthorize("hasAnyRole('DELIVERY', 'ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Auto-asignar repartidor a un pedido (DELIVERY / ADMIN)")
    public ResponseEntity<Object> assignDelivery(@Valid @RequestBody AssignDeliveryRequest body, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/deliveries/assign", req, body);
    }

    // ========== AFFILIATE / GENERIC LOGISTICS ==========

    @GetMapping("/orders/{orderId}/status")
    @PreAuthorize("hasAnyRole('AFFILIATE', 'ADMIN', 'DELIVERY')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Estado del pedido para seguimiento (AFFILIATE / DELIVERY / ADMIN)")
    public ResponseEntity<Object> getOrderStatus(@PathVariable Long orderId, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/orders/" + orderId + "/status", req, null);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('AFFILIATE', 'ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Dashboard de logística (AFFILIATE/ADMIN)")
    public ResponseEntity<Object> getDashboard(HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/dashboard", req, null);
    }

    @PostMapping("/orders")
    @PreAuthorize("hasRole('AFFILIATE')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Crear orden de logística (AFFILIATE ONLY)")
    public ResponseEntity<Object> createLogisticsOrder(@RequestBody Object body, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/orders", req, body);
    }

    @PostMapping("/assignments")
    @PreAuthorize("hasRole('AFFILIATE')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Asignar courier (AFFILIATE ONLY)")
    public ResponseEntity<Object> assignCourier(@RequestBody Object body, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/assignments", req, body);
    }

    // ========== LEGACY (No en especificación) ==========

    @GetMapping("/deliveries/{id}/location")
    @PreAuthorize("hasRole('DELIVERY')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "[DEPRECADO] Ubicación de entrega - usar PUT /location")
    public ResponseEntity<Object> location(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/deliveries/" + id + "/location", req, null);
    }
}
