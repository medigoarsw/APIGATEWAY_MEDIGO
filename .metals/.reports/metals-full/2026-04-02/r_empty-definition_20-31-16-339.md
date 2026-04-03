error id: file:///D:/ander/Documents/SEMESTRE%207/ARSW/PROYECTO%20OFICIAL/APIGATEWAY_MEDIGO/src/main/java/com/medigo/gateway/infrastructure/adapter/in/AuctionController.java:io/swagger/v3/oas/annotations/security/SecurityRequirement#
file:///D:/ander/Documents/SEMESTRE%207/ARSW/PROYECTO%20OFICIAL/APIGATEWAY_MEDIGO/src/main/java/com/medigo/gateway/infrastructure/adapter/in/AuctionController.java
empty definition using pc, found symbol in pc: io/swagger/v3/oas/annotations/security/SecurityRequirement#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 493
uri: file:///D:/ander/Documents/SEMESTRE%207/ARSW/PROYECTO%20OFICIAL/APIGATEWAY_MEDIGO/src/main/java/com/medigo/gateway/infrastructure/adapter/in/AuctionController.java
text:
```scala
package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.application.dto.request.CreateAuctionRequest;
import com.medigo.gateway.application.dto.request.PlaceBidRequest;
import com.medigo.gateway.application.dto.request.UpdateAuctionRequest;
import com.medigo.gateway.application.service.ValidationService;
import com.medigo.gateway.domain.port.in.ForwardingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.@@SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de subastas: gestión de subastas y pujas (HU-15 a HU-22).
 */
@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
@Tag(name = "Auctions", description = "Gestión de subastas")
@SecurityRequirement(name = "BearerAuth")
public class AuctionController {

    private final ForwardingUseCase forwardingUseCase;
    private final ValidationService validationService;

    // ===== SUBASTAS ACTIVAS Y DETALLES =====

    @GetMapping("/active")
    @Operation(summary = "Listar subastas activas (HU-17)")
    public ResponseEntity<Object> active(HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/active", req, null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de subasta (HU-17)")
    public ResponseEntity<Object> getById(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/" + id, req, null);
    }

    // ===== HISTORIAL DE PUJAS =====

    @GetMapping("/{id}/bids")
    @Operation(summary = "Historial de pujas en subasta (HU-17)")
    public ResponseEntity<Object> getBids(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/" + id + "/bids", req, null);
    }

    @GetMapping("/{id}/winner")
    @Operation(summary = "Obtener ganador de la subasta (HU-22)")
    public ResponseEntity<Object> getWinner(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/" + id + "/winner", req, null);
    }

    // ===== ADMIN: CREAR Y EDITAR SUBASTAS =====

    @PostMapping
    @Operation(summary = "Crear subasta (ADMIN - HU-15)")
    public ResponseEntity<Object> create(
            @Valid @RequestBody CreateAuctionRequest body, HttpServletRequest req) {
        validationService.validateCreateAuction(body);
        return forwardingUseCase.forward("/api/auctions", req, body);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar subasta programada (ADMIN - HU-16)")
    public ResponseEntity<Object> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAuctionRequest body,
            HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/" + id, req, body);
    }

    // ===== USUARIO: UNIRSE Y PUJAR =====

    @PostMapping("/{id}/join")
    @Operation(summary = "Unirse a la subasta (HU-18)")
    public ResponseEntity<Object> join(
            @PathVariable Long id,
            @RequestParam Long userId,
            HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/" + id + "/join?userId=" + userId, req, null);
    }

    @PostMapping("/{id}/bids")
    @Operation(summary = "Colocar puja en subasta (HU-19)")
    public ResponseEntity<Object> placeBid(
            @PathVariable Long id,
            @Valid @RequestBody PlaceBidRequest body,
            HttpServletRequest req) {
        return forwardingUseCase.forward("/api/auctions/" + id + "/bids", req, body);
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: io/swagger/v3/oas/annotations/security/SecurityRequirement#