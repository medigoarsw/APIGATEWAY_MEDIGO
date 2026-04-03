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
 * Controller del catálogo de medicamentos: búsqueda, crear, editar stock.
 */
@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
@Tag(name = "Catalog", description = "Catálogo de medicamentos")
public class CatalogController {

    private final ForwardingUseCase forwardingUseCase;

    // ===== BÚSQUEDA PÚBLICA (sin JWT) =====

    @GetMapping("/medications/search")
    @Operation(summary = "Buscar medicamentos por nombre (Público)")
    public ResponseEntity<Object> search(
            @RequestParam String name,
            HttpServletRequest req) {
        return forwardingUseCase.forward("/api/catalog/medications/search?name=" + name, req, null);
    }

    @GetMapping("/medications/branch/{branchId}/stock")
    @Operation(summary = "Stock en sucursal (Público)")
    public ResponseEntity<Object> getStockByBranch(
            @PathVariable Long branchId,
            HttpServletRequest req) {
        return forwardingUseCase.forward(
                "/api/catalog/medications/branch/" + branchId + "/stock", req, null);
    }

    @GetMapping("/medications/branch/{branchId}/medications")
    @Operation(summary = "Medicamentos de sucursal (Público)")
    public ResponseEntity<Object> getMedicationsByBranch(
            @PathVariable Long branchId,
            HttpServletRequest req) {
        return forwardingUseCase.forward(
                "/api/catalog/medications/branch/" + branchId + "/medications", req, null);
    }

    @GetMapping("/medications/branches")
    @Operation(summary = "Listar sucursales (Público)")
    public ResponseEntity<Object> getBranches(HttpServletRequest req) {
        return forwardingUseCase.forward("/api/catalog/medications/branches", req, null);
    }

    // ===== PROTEGIDOS (JWT requerido) =====

    @GetMapping("/medications/{id}")
    @Operation(summary = "Obtener medicamento por ID")
    public ResponseEntity<Object> getById(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/catalog/medications/" + id, req, null);
    }

    @GetMapping("/medications")
    @Operation(summary = "Listar todos los medicamentos")
    public ResponseEntity<Object> list(HttpServletRequest req) {
        return forwardingUseCase.forward("/api/catalog/medications", req, null);
    }

    @PostMapping("/medications")
    @Operation(summary = "Crear medicamento (ADMIN - HU-07)")
    public ResponseEntity<Object> create(@RequestBody Object body, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/catalog/medications", req, body);
    }

    @PutMapping("/medications/{id}")
    @Operation(summary = "Actualizar medicamento (ADMIN)")
    public ResponseEntity<Object> update(
            @PathVariable Long id,
            @RequestBody Object body,
            HttpServletRequest req) {
        return forwardingUseCase.forward("/api/catalog/medications/" + id, req, body);
    }

    @PutMapping("/medications/{id}/stock")
    @Operation(summary = "Editar stock por sucursal (ADMIN - HU-08)")
    public ResponseEntity<Object> updateStock(
            @PathVariable Long id,
            @RequestBody Object body,
            HttpServletRequest req) {
        return forwardingUseCase.forward("/api/catalog/medications/" + id + "/stock", req, body);
    }
}
