package com.medigo.gateway.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI 3 con soporte JWT Bearer.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI medigoOpenAPI() {
        final String schemeName = "BearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("MediGo API Gateway")
                        .version("1.0.0")
                        .description("Reverse proxy seguro para la plataforma MediGo")
                        .contact(new Contact().name("MediGo Team")))
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components().addSecuritySchemes(schemeName,
                        new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
