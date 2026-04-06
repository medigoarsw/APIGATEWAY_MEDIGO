error id: file:///D:/ander/Documents/SEMESTRE%207/ARSW/PROYECTO%20OFICIAL/APIGATEWAY_MEDIGO/src/main/java/com/medigo/gateway/infrastructure/config/SecurityConfig.java:_empty_/`<any>`#requestMatchers#permitAll#requestMatchers#permitAll#requestMatchers#permitAll#requestMatchers#permitAll#anyRequest#authenticated#
file:///D:/ander/Documents/SEMESTRE%207/ARSW/PROYECTO%20OFICIAL/APIGATEWAY_MEDIGO/src/main/java/com/medigo/gateway/infrastructure/config/SecurityConfig.java
empty definition using pc, found symbol in pc: _empty_/`<any>`#requestMatchers#permitAll#requestMatchers#permitAll#requestMatchers#permitAll#requestMatchers#permitAll#anyRequest#authenticated#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 2639
uri: file:///D:/ander/Documents/SEMESTRE%207/ARSW/PROYECTO%20OFICIAL/APIGATEWAY_MEDIGO/src/main/java/com/medigo/gateway/infrastructure/config/SecurityConfig.java
text:
```scala
package com.medigo.gateway.infrastructure.config;

import com.medigo.gateway.infrastructure.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de Spring Security: stateless JWT + RBAC.
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Públicos
                .requestMatchers("/", "/error").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/**").permitAll()

                // TODO: descomentar las líneas de roles cuando el backend active su propia seguridad.
                // Los roles que devuelve el backend son: "ADMIN", "USER", "DELIVERY".
                // Ejemplo de cómo quedaría:
                //   .requestMatchers(HttpMethod.POST, "/api/auctions").hasRole("ADMIN")
                //   .requestMatchers(HttpMethod.PUT,  "/api/auctions/**").hasRole("ADMIN")
                //   .requestMatchers(HttpMethod.DELETE, "/api/auctions/**").hasRole("ADMIN")
                //   .requestMatchers("/api/catalog/**").hasRole("ADMIN")
                //   .requestMatchers("/api/auctions/**").hasAnyRole("ADMIN", "USER")
                //   .requestMatchers("/api/orders/**").hasAnyRole("ADMIN", "USER")
                //   .requestMatchers("/api/logistics/**").hasAnyRole("ADMIN", "DELIVERY")

                // Por ahora: cualquier usuario autenticado puede acceder
                .anyRequest().@@authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/`<any>`#requestMatchers#permitAll#requestMatchers#permitAll#requestMatchers#permitAll#requestMatchers#permitAll#anyRequest#authenticated#