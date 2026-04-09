package com.medigo.gateway.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * Configuración del RestTemplate con timeout definido en properties.
 */
@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final GatewayProperties properties;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        int ms = properties.getBackend().getTimeoutSeconds() * 1000;
        factory.setConnectTimeout(ms);
        factory.setReadTimeout(ms);
        // Evita fallos "cannot retry due to server authentication, in streaming mode"
        // al reenviar POST/PUT cuando el backend responde 401/403.
        factory.setOutputStreaming(false);
        RestTemplate restTemplate = new RestTemplate(factory);
        // No lanzar excepción en errores 4xx/5xx del backend; dejar que el servicio los maneje
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }
        });
        return restTemplate;
    }
}
