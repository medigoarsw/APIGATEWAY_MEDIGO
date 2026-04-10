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

    @PostMapping("/deliveries/accept")
    @PreAuthorize("hasRole('DELIVERY')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Aceptar un pedido disponible (DELIVERY ONLY)")
    public ResponseEntity<Object> acceptDelivery(
            @RequestParam Long orderId, 
            @RequestParam(required = false) Long driverId, 
            HttpServletRequest req) {
        
        // Si no viene driverId en el query, lo pasamos como null y el backend debería extraerlo del token
        String targetPath = "/api/logistics/deliveries/accept?orderId=" + orderId;
        if (driverId != null) {
            targetPath += "&driverId=" + driverId;
        }
        
        return forwardingUseCase.forward(targetPath, req, null);
    }

    @PutMapping("/deliveries/{id}/pickup")
    @PreAuthorize("hasRole('DELIVERY')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Marcar pedido como recogido (DELIVERY ONLY)")
    public ResponseEntity<Object> pickupDelivery(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/deliveries/" + id + "/pickup", req, null);
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

    // ========== ADMIN ==========

    @PostMapping("/deliveries/assign")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Asignar entrega a repartidor (ADMIN ONLY)")
    public ResponseEntity<Object> assignDelivery(@Valid @RequestBody AssignDeliveryRequest body, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/deliveries/assign", req, body);
    }

    // ========== AFFILIATE ==========

    @GetMapping("/affiliate/dashboard")
    @PreAuthorize("hasRole('AFFILIATE')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Obtener dashboard logístico (AFFILIATE ONLY)")
    public ResponseEntity<Object> getAffiliateDashboard(
            @RequestParam Long affiliateId, 
            HttpServletRequest req) {
        org.slf4j.LoggerFactory.getLogger(LogisticsController.class).info("Gateway LogisticsController: Received /affiliate/dashboard request for affiliateId={}. Forwarding to backend...", affiliateId);
        return forwardingUseCase.forward("/api/logistics/affiliate/dashboard?affiliateId=" + affiliateId, req, null);
    }

    // ========== LEGACY (No en especificación) ==========

    @GetMapping("/deliveries/{id}/location")
    @PreAuthorize("hasRole('DELIVERY')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "[DEPRECADO] Ubicación de entrega - usar PUT /location")
    public ResponseEntity<Object> location(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/deliveries/" + id + "/location", req, null);
    }

    // ========== TEST ==========

    @GetMapping("/test-backend")
    @Operation(summary = "Punto de prueba sin seguridad (PUBLIC)")
    public ResponseEntity<Object> testBackend(@RequestParam Long affiliateId, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/affiliate/dashboard?affiliateId=" + affiliateId, req, null);
    }
}
