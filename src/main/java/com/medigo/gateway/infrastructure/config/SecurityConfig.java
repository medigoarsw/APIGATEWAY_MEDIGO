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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ======== PÚBLICOS ========
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/medications/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/medications/branch/*/stock").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/medications/branch/*/medications").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/medications/branches").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/medications/*/availability/branch/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/medications/*/availability/branches").permitAll()
                
                // Swagger, actuator
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/health").permitAll()

                // ======== AUTHENTICATED (requieren JWT) ========
                .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()

                // ======== ADMIN ONLY ========
                .requestMatchers(HttpMethod.GET, "/api/auth/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/auth/email/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/medications").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/medications/*/branch/*/stock").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/auctions").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/auctions/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/logistics/deliveries/assign").hasRole("ADMIN")

                // ======== AFFILIATE ONLY ========
                .requestMatchers(HttpMethod.POST, "/api/orders/cart/add").hasRole("AFFILIATE")
                .requestMatchers(HttpMethod.GET, "/api/orders/cart").hasRole("AFFILIATE")
                .requestMatchers(HttpMethod.POST, "/api/orders").hasRole("AFFILIATE")
                .requestMatchers(HttpMethod.POST, "/api/orders/*/confirm").hasRole("AFFILIATE")

                // ======== ADMIN O AFFILIATE ========
                .requestMatchers(HttpMethod.GET, "/api/auctions/{id}").hasAnyRole("ADMIN", "AFFILIATE")
                .requestMatchers(HttpMethod.GET, "/api/auctions/active").hasAnyRole("ADMIN", "AFFILIATE")
                .requestMatchers(HttpMethod.GET, "/api/auctions/{id}/bids").hasAnyRole("ADMIN", "AFFILIATE")
                .requestMatchers(HttpMethod.GET, "/api/auctions/{id}/winner").hasAnyRole("ADMIN", "AFFILIATE")
                .requestMatchers(HttpMethod.POST, "/api/auctions/{id}/join").hasAnyRole("ADMIN", "AFFILIATE")
                .requestMatchers(HttpMethod.POST, "/api/auctions/{id}/bids").hasAnyRole("ADMIN", "AFFILIATE")

                // ======== DELIVERY ONLY ========
                .requestMatchers(HttpMethod.PUT, "/api/logistics/deliveries/{id}/location").hasRole("DELIVERY")
                .requestMatchers(HttpMethod.PUT, "/api/logistics/deliveries/{id}/complete").hasRole("DELIVERY")
                .requestMatchers(HttpMethod.GET, "/api/logistics/deliveries/active").hasRole("DELIVERY")
                .requestMatchers(HttpMethod.GET, "/api/logistics/deliveries/{id}").hasRole("DELIVERY")

                // Cualquier otra ruta: requiere autenticación
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
