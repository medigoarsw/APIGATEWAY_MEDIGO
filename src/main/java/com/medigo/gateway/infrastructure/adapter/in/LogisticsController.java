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
 * Controller de logística y ubicación de entregas.
 */
@RestController
@RequestMapping("/api/logistics")
@RequiredArgsConstructor
@Tag(name = "Logistics", description = "Logística de entregas")
@SecurityRequirement(name = "BearerAuth")
public class LogisticsController {

    private final ForwardingUseCase forwardingUseCase;

    @GetMapping("/deliveries/{id}/location")
    @Operation(summary = "Ubicación de entrega en tiempo real")
    public ResponseEntity<Object> location(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/deliveries/" + id + "/location", req, null);
    }

    @PutMapping("/deliveries/{id}/location")
    @Operation(summary = "Actualizar ubicación (REPARTIDOR)")
    public ResponseEntity<Object> updateLocation(
            @PathVariable Long id, @RequestBody Object body, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/deliveries/" + id + "/location", req, body);
    }

    @GetMapping("/deliveries/{id}")
    @Operation(summary = "Estado de entrega")
    public ResponseEntity<Object> status(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/logistics/deliveries/" + id, req, null);
    }
}
