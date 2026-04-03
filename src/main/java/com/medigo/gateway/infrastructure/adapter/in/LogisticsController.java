package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.domain.port.in.ForwardingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de logística: entregas y ubicación en tiempo real (REPARTIDOR).
 */
@RestController
@RequestMapping("/api/logistics")
@RequiredArgsConstructor
@Tag(name = "Logistics", description = "Logística de entregas (REPARTIDOR)")
@SecurityRequirement(name = "BearerAuth")
public class LogisticsController {

    private final ForwardingUseCase forwardingUseCase;

    @GetMapping("/deliveries/active")
    @Operation(summary = "Entregas activas del repartidor (HU-11)")
    public ResponseEntity<Object> getActiveDeliveries(HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/deliveries/active", req, null);
    }

    @GetMapping("/deliveries/{id}")
    @Operation(summary = "Detalle de entrega (HU-11)")
    public ResponseEntity<Object> getDelivery(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/deliveries/" + id, req, null);
    }

    @PutMapping("/deliveries/{id}/complete")
    @Operation(summary = "Confirmar entrega completada (HU-10)")
    public ResponseEntity<Object> completeDelivery(
            @PathVariable Long id,
            @RequestBody Object body,
            HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/deliveries/" + id + "/complete", req, body);
    }

    @GetMapping("/deliveries/{id}/location")
    @Operation(summary = "Ubicación GPS en tiempo real")
    public ResponseEntity<Object> getLocation(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/deliveries/" + id + "/location", req, null);
    }

    @PutMapping("/deliveries/{id}/location")
    @Operation(summary = "Actualizar ubicación GPS del repartidor")
    public ResponseEntity<Object> updateLocation(
            @PathVariable Long id, @RequestBody Object body, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/deliveries/" + id + "/location", req, body);
    }
}
