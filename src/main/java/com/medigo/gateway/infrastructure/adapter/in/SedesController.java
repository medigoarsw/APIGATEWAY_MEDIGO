package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.domain.port.in.ForwardingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Administración de sedes (solo ADMIN).
 * Ruta canónica única: /api/sedes
 */
@RestController
@RequestMapping("/api/sedes")
@RequiredArgsConstructor
@Validated
@Tag(name = "Sedes", description = "Administración de sedes (ADMIN)")
public class SedesController {

    private final ForwardingUseCase forwardingUseCase;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Listar sedes con paginación y filtro (ADMIN ONLY)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    public ResponseEntity<Object> list(
            @Parameter(description = "Página (default 1, mínimo 1)")
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @Parameter(description = "Tamaño de página (default 20, rango 1..100)")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
            @Parameter(description = "Filtro opcional por nombre, dirección o especialidad")
            @RequestParam(required = false) String q,
            HttpServletRequest req) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath("/api/sedes")
                .queryParam("page", page)
                .queryParam("limit", limit);

        if (q != null && !q.isBlank()) {
            builder.queryParam("q", q);
        }

        return forwardingUseCase.forward(builder.toUriString(), req, null);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Obtener sede por id (ADMIN ONLY)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta exitosa"),
            @ApiResponse(responseCode = "404", description = "Sede no encontrada")
    })
    public ResponseEntity<Object> getById(
            @Parameter(description = "Id de la sede") @PathVariable Long id,
            HttpServletRequest req) {
        return forwardingUseCase.forward("/api/sedes/" + id, req, null);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Crear sede (ADMIN ONLY)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sede creada"),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "409", description = "Conflicto")
    })
    public ResponseEntity<Object> create(
            @Valid @RequestBody CreateSedeRequest body,
            HttpServletRequest req) {
        return forwardingUseCase.forward("/api/sedes", req, body);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Actualizar sede (parcial con PUT) (ADMIN ONLY)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sede actualizada"),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "404", description = "Sede no encontrada"),
            @ApiResponse(responseCode = "409", description = "Conflicto")
    })
    public ResponseEntity<Object> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSedeRequest body,
            HttpServletRequest req) {
        return forwardingUseCase.forward("/api/sedes/" + id, req, body);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Eliminar sede (soft delete) (ADMIN ONLY)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sede eliminada lógicamente"),
            @ApiResponse(responseCode = "404", description = "Sede no encontrada")
    })
    public ResponseEntity<Object> delete(
            @PathVariable Long id,
            HttpServletRequest req) {
        return forwardingUseCase.forward("/api/sedes/" + id, req, null);
    }

    @Data
    @Schema(name = "CreateSedeRequest")
    static class CreateSedeRequest {
        @NotBlank(message = "nombre es requerido")
        private String nombre;
        @NotBlank(message = "direccion es requerida")
        private String direccion;
        @NotBlank(message = "especialidad es requerida")
        private String especialidad;
        private String telefono;
        @PositiveOrZero(message = "capacidad debe ser mayor o igual a 0")
        private Integer capacidad;
    }

    @Data
    @Schema(name = "UpdateSedeRequest")
    static class UpdateSedeRequest {
        private String nombre;
        private String direccion;
        private String especialidad;
        private String telefono;
        @PositiveOrZero(message = "capacidad debe ser mayor o igual a 0")
        private Integer capacidad;
    }

    @Data
    @Schema(name = "GatewayStandardResponse")
    static class GatewayStandardResponse {
        private boolean success;
        private String message;
        @Schema(description = "Objeto de respuesta o null")
        private Object data;
        private String traceId;
        private String apiVersion;
        private String timestamp;
    }
}
