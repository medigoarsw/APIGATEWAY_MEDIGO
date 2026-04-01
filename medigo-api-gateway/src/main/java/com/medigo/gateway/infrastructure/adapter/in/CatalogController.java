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
 * Controller del catálogo de medicamentos.
 */
@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
@Tag(name = "Catalog", description = "Catálogo de medicamentos")
@SecurityRequirement(name = "BearerAuth")
public class CatalogController {

    private final ForwardingUseCase forwardingUseCase;

    @GetMapping("/medications")
    @Operation(summary = "Listar medicamentos")
    public ResponseEntity<Object> list(HttpServletRequest req) {
        return forwardingUseCase.forward("/api/catalog/medications", req, null);
    }

    @GetMapping("/medications/{id}")
    @Operation(summary = "Obtener medicamento por ID")
    public ResponseEntity<Object> getById(@PathVariable Long id, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/catalog/medications/" + id, req, null);
    }

    @PostMapping("/medications")
    @Operation(summary = "Crear medicamento (ADMIN)")
    public ResponseEntity<Object> create(@RequestBody Object body, HttpServletRequest req) {
        return forwardingUseCase.forward("/api/catalog/medications", req, body);
    }
}
