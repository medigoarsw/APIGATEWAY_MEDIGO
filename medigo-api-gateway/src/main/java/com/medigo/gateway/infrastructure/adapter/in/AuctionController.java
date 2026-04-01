package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.application.dto.request.CreateAuctionRequest;
import com.medigo.gateway.application.dto.request.PlaceBidRequest;
import com.medigo.gateway.application.service.ValidationService;
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
 * Controller de subastas: enruta hacia el backend.
 */
@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
@Tag(name = "Auctions", description = "Gestión de subastas")
@SecurityRequirement(name = "BearerAuth")
public class AuctionController {

    private final ForwardingUseCase forwardingUseCase;
    private final ValidationService validationService;

    @GetMapping
    @Operation(summary = "Listar subastas")
    public ResponseEntity<Object> list(HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions", req, null);
    }

    @GetMapping("/active")
    @Operation(summary = "Subastas activas")
    public ResponseEntity<Object> active(HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/active", req, null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener subasta por ID")
    public ResponseEntity<Object> getById(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/" + id, req, null);
    }

    @PostMapping
    @Operation(summary = "Crear subasta (ADMIN)")
    public ResponseEntity<Object> create(
            @Valid @RequestBody CreateAuctionRequest body, HttpServletRequest req) {
        validationService.validateCreateAuction(body);
        return forwardingUseCase.forward("/api/auctions", req, body);
    }

    @PostMapping("/{id}/bids")
    @Operation(summary = "Colocar puja")
    public ResponseEntity<Object> placeBid(
            @PathVariable Long id,
            @Valid @RequestBody PlaceBidRequest body,
            HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/" + id + "/bids", req, body);
    }

    @GetMapping("/{id}/bids")
    @Operation(summary = "Historial de pujas")
    public ResponseEntity<Object> getBids(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/" + id + "/bids", req, null);
    }
}
