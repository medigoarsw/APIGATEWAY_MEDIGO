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
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/health").permitAll()

                // Solo ADMIN
                .requestMatchers(HttpMethod.POST, "/api/auctions").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/auctions/**").hasRole("ADMIN")
                .requestMatchers("/api/catalog/**").hasRole("ADMIN")

                // ADMIN y USUARIO
                .requestMatchers("/api/auctions/**").hasAnyRole("ADMIN", "USUARIO")
                .requestMatchers("/api/orders/**").hasAnyRole("ADMIN", "USUARIO")

                // ADMIN y REPARTIDOR
                .requestMatchers("/api/logistics/**").hasAnyRole("ADMIN", "REPARTIDOR")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
