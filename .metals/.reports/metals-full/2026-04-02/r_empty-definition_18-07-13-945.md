error id: file:///D:/ander/Documents/SEMESTRE%207/ARSW/PROYECTO%20OFICIAL/APIGATEWAY_MEDIGO/src/main/java/com/medigo/gateway/infrastructure/interceptor/WebMvcConfig.java:java/util/Optional#ifPresent().
file:///D:/ander/Documents/SEMESTRE%207/ARSW/PROYECTO%20OFICIAL/APIGATEWAY_MEDIGO/src/main/java/com/medigo/gateway/infrastructure/interceptor/WebMvcConfig.java
empty definition using pc, found symbol in pc: java/util/Optional#ifPresent().
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 987
uri: file:///D:/ander/Documents/SEMESTRE%207/ARSW/PROYECTO%20OFICIAL/APIGATEWAY_MEDIGO/src/main/java/com/medigo/gateway/infrastructure/interceptor/WebMvcConfig.java
text:
```scala
package com.medigo.gateway.infrastructure.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Optional;

/**
 * Registro de interceptores en el pipeline MVC.
 * Orden: RateLimitInterceptor → AuditLoggingInterceptor
 * Los interceptores son opcionales para soportar tests sin capa de datos.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final Optional<RateLimitInterceptor> rateLimitInterceptor;
    private final Optional<AuditLoggingInterceptor> auditLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Rate Limiting: control de frecuencia de peticiones (si está disponible)
        rateLimitInterceptor.@@ifPresent(interceptor ->
            registry.addInterceptor(interceptor)
                    .addPathPatterns("/api/**")
                    .excludePathPatterns("/api/auth/login", "/api/auth/register")
        );

        // Auditoría: registra todas las peticiones en BD (si está disponible)
        auditLoggingInterceptor.ifPresent(interceptor ->
            registry.addInterceptor(interceptor)
                    .addPathPatterns("/api/**")
                    .order(1) // Se ejecuta después del rate limiting
        );
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: java/util/Optional#ifPresent().