package com.medigo.gateway.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Warmup: Espera a que el Backend esté completamente listo antes de servir peticiones.
 * Evita condiciones de carrera (race conditions) donde la primera petición falla
 * porque el Backend/Gateway no está 100% inicializado.
 */
@Slf4j
@Component
public class GatewayWarmup {

    private final RestTemplate restTemplate;
    private final GatewayProperties properties;

    public GatewayWarmup(RestTemplate restTemplate, GatewayProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warmupBackend() throws InterruptedException {
        String backendUrl = properties.getBackend().getBaseUrl();
        String readinessUrl = backendUrl + "/internal/ready";

        log.info("Gateway warmup: Esperando que Backend esté listo en {}", readinessUrl);

        int maxAttempts = 20;
        int attempt = 0;
        long delayMs = 1000;  // Aumentado de 500ms a 1000ms

        while (attempt < maxAttempts) {
            try {
                var response = restTemplate.getForObject(readinessUrl, String.class);
                if ("READY".equals(response)) {
                    log.info("✓ Backend respondió READY. Esperando estabilización de conexiones...");
                    Thread.sleep(3000);  // Espera adicional 3 segundos después de ready
                    log.info("✓ Backend está listo. Gateway completamente operativo.");
                    return;
                }
            } catch (Exception e) {
                attempt++;
                if (attempt < maxAttempts) {
                    log.debug("Backend aún no listo (intento {}/{}). Esperando {}ms...", 
                        attempt, maxAttempts, delayMs);
                    Thread.sleep(delayMs);
                }
            }
        }

        log.warn("⚠ Timeout esperando que Backend esté listo. Continuando de todas formas...");
    }
}
