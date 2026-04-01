package com.medigo.gateway.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

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
        return new RestTemplate(factory);
    }
}
