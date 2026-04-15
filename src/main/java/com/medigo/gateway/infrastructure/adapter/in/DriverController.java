package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.domain.model.UserClaims;
import com.medigo.gateway.domain.port.in.ForwardingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller del repartidor (DELIVERY).
 * Historial de viajes y soporte de emergencia.
 */
@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
@Tag(name = "Driver", description = "Historial y soporte del repartidor")
public class DriverController {

    private final ForwardingUseCase forwardingUseCase;

    @GetMapping("/history/summary")
    @PreAuthorize("hasRole('DELIVERY')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Resumen de historial del repartidor (DELIVERY ONLY)")
    public ResponseEntity<Object> getHistorySummary(
            @RequestParam(required = false) String range,
            Authentication authentication,
            HttpServletRequest req) {

        UserClaims claims = (UserClaims) authentication.getPrincipal();
        String userId = claims.getUserId();
        String query = "?deliveryPersonId=" + userId + (range != null ? "&range=" + range : "");
        return forwardingUseCase.forward("/api/logistics/deliveries/history/summary" + query, req, null);
    }

    @GetMapping("/history/trips")
    @PreAuthorize("hasRole('DELIVERY')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Lista de viajes del repartidor (DELIVERY ONLY)")
    public ResponseEntity<Object> getHistoryTrips(
            @RequestParam(required = false) String range,
            Authentication authentication,
            HttpServletRequest req) {

        UserClaims claims = (UserClaims) authentication.getPrincipal();
        String userId = claims.getUserId();
        String query = "?deliveryPersonId=" + userId + (range != null ? "&range=" + range : "");
        return forwardingUseCase.forward("/api/logistics/deliveries/history" + query, req, null);
    }

    @PostMapping("/support/emergency")
    @PreAuthorize("hasRole('DELIVERY')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Reportar soporte de emergencia (DELIVERY ONLY)")
    public ResponseEntity<Object> emergencySupport(
            @RequestBody(required = false) Object body,
            HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/driver/emergency", req, body);
    }
}
