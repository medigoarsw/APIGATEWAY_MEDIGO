package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.application.dto.request.CreateAuctionRequest;
import com.medigo.gateway.application.dto.request.PlaceBidRequest;
import com.medigo.gateway.application.dto.request.UpdateAuctionRequest;
import com.medigo.gateway.application.service.ValidationService;
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
 * Controller de subastas.
 * ADMIN: crear, editar subastas.
 * ADMIN + AFFILIATE: ver subastas, pujar, unirse.
 */
@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
@Tag(name = "Auctions", description = "Gestión de subastas")
public class AuctionController {

    private final ForwardingUseCase forwardingUseCase;
    private final ValidationService validationService;

    // ========== ADMIN ONLY ==========

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Crear subasta (ADMIN ONLY)")
    public ResponseEntity<Object> create(
            @Valid @RequestBody CreateAuctionRequest body, HttpServletRequest req) {
        validationService.validateCreateAuction(body);
        return forwardingUseCase.forward("/api/auctions", req, body);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Editar subasta (ADMIN ONLY)")
    public ResponseEntity<Object> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAuctionRequest body,
            HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/" + id, req, body);
    }

    // ========== ADMIN + AFFILIATE ==========

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'AFFILIATE')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Subastas activas (ADMIN + AFFILIATE)")
    public ResponseEntity<Object> active(HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/active", req, null);
    }

    @GetMapping("/won")
    @PreAuthorize("hasAnyRole('ADMIN', 'AFFILIATE')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Subastas ganadas del usuario autenticado (ADMIN + AFFILIATE)")
    public ResponseEntity<Object> won(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/won?page=" + page + "&size=" + size, req, null);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AFFILIATE')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Obtener subasta por ID (ADMIN + AFFILIATE)")
    public ResponseEntity<Object> getById(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/" + id, req, null);
    }

    @GetMapping("/{id}/bids")
    @PreAuthorize("hasAnyRole('ADMIN', 'AFFILIATE')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Historial de pujas (ADMIN + AFFILIATE)")
    public ResponseEntity<Object> getBids(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/" + id + "/bids", req, null);
    }

    @GetMapping("/{id}/winner")
    @PreAuthorize("hasAnyRole('ADMIN', 'AFFILIATE')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Ganador de subasta (ADMIN + AFFILIATE)")
    public ResponseEntity<Object> getWinner(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/" + id + "/winner", req, null);
    }

    @PostMapping("/{id}/join")
    @PreAuthorize("hasAnyRole('ADMIN', 'AFFILIATE')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Unirse a subasta (ADMIN + AFFILIATE)")
    public ResponseEntity<Object> join(
            @PathVariable Long id,
            @RequestParam Long userId,
            HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/" + id + "/join?userId=" + userId, req, null);
    }

    @PostMapping("/{id}/bids")
    @PreAuthorize("hasAnyRole('ADMIN', 'AFFILIATE')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Colocar puja (ADMIN + AFFILIATE)")
    public ResponseEntity<Object> placeBid(
            @PathVariable Long id,
            @Valid @RequestBody PlaceBidRequest body,
            HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/" + id + "/bids", req, body);
    }
}
