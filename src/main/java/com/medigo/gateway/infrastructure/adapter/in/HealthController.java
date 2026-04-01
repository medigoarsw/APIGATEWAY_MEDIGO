package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.application.dto.response.GatewayResponse;
import com.medigo.gateway.infrastructure.common.GatewayConstants;
import com.medigo.gateway.infrastructure.common.TraceIdHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint de salud del gateway.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Health", description = "Estado del gateway")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Health check del gateway")
    public ResponseEntity<GatewayResponse<Map<String, String>>> health() {
        Map<String, String> info = Map.of(
                "status", "UP",
                "service", "MediGo API Gateway",
                "version", GatewayConstants.API_VERSION
        );
        return ResponseEntity.ok(GatewayResponse.ok(info, TraceIdHolder.get()));
    }
}
