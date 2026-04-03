package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.application.dto.request.CreateMedicationRequest;
import com.medigo.gateway.application.dto.request.UpdateStockRequest;
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
 * Controller del catálogo de medicamentos.
 * Rutas públicas: búsqueda, availability, branch info.
 * Rutas protegidas ADMIN: crear/actualizar medicamentos.
 */
@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
@Tag(name = "Catalog", description = "Catálogo de medicamentos")
public class CatalogController {

    private final ForwardingUseCase forwardingUseCase;

    // ========== PÚBLICOS ==========

    @GetMapping("/search")
    @Operation(summary = "Buscar medicamentos por nombre (PUBLIC)")
    public ResponseEntity<Object> search(@RequestParam String name, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/medications/search?name=" + name, req, null);
    }

    @GetMapping("/branch/{branchId}/stock")
    @Operation(summary = "Stock de medicamentos en sucursal (PUBLIC)")
    public ResponseEntity<Object> branchStock(@PathVariable Long branchId, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/medications/branch/" + branchId + "/stock", req, null);
    }

    @GetMapping("/branch/{branchId}/medications")
    @Operation(summary = "Medicamentos disponibles en sucursal (PUBLIC)")
    public ResponseEntity<Object> branchMedications(@PathVariable Long branchId, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/medications/branch/" + branchId + "/medications", req, null);
    }

    @GetMapping("/branches")
    @Operation(summary = "Listar sucursales con medicamentos (PUBLIC)")
    public ResponseEntity<Object> listBranches(HttpServletRequest req) {
        return forwardingUseCase.forward("/api/medications/branches", req, null);
    }

    @GetMapping("/{medicationId}/availability/branch/{branchId}")
    @Operation(summary = "Verificar disponibilidad en sucursal (PUBLIC)")
    public ResponseEntity<Object> availabilityByBranch(
            @PathVariable Long medicationId,
            @PathVariable Long branchId,
            HttpServletRequest req) {
        return forwardingUseCase.forward(
                "/api/medications/" + medicationId + "/availability/branch/" + branchId,
                req, null);
    }

    @GetMapping("/{medicationId}/availability/branches")
    @Operation(summary = "Disponibilidad en todas las sucursales (PUBLIC)")
    public ResponseEntity<Object> availabilityAllBranches(
            @PathVariable Long medicationId,
            HttpServletRequest req) {
        return forwardingUseCase.forward(
                "/api/medications/" + medicationId + "/availability/branches",
                req, null);
    }

    // ========== PROTEGIDOS ADMIN ==========

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Crear medicamento (ADMIN ONLY)")
    public ResponseEntity<Object> create(@Valid @RequestBody CreateMedicationRequest body, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/medications", req, body);
    }

    @PutMapping("/{medicationId}/branch/{branchId}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Actualizar stock en sucursal (ADMIN ONLY)")
    public ResponseEntity<Object> updateStock(
            @PathVariable Long medicationId,
            @PathVariable Long branchId,
            @Valid @RequestBody UpdateStockRequest body,
            HttpServletRequest req) {
        return forwardingUseCase.forward(
                "/api/medications/" + medicationId + "/branch/" + branchId + "/stock",
                req, body);
    }

    // ========== LEGACY (No en especificación, mantener pero deprecar) ==========

    @GetMapping
    @Operation(summary = "[DEPRECADO] Listar medicamentos - usar /search")
    public ResponseEntity<Object> list(HttpServletRequest req) {
        return forwardingUseCase.forward("/api/medications", req, null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "[DEPRECADO] Obtener medicamento - usar /availability")
    public ResponseEntity<Object> getById(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/medications/" + id, req, null);
    }
}
