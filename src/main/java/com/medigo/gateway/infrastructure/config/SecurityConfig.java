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

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuración de Spring Security: stateless JWT + RBAC + CORS.
 * 
 * CORS Configuration:
 * - Permite preflight OPTIONS sin autenticación
 * - Soporta múltiples orígenes de Vercel (*.vercel.app)
 * - Permite desarrollo local (localhost:5173, etc)
 * - Establece headers Security para producción
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ======== PREFLIGHT OPTIONS (SIN AUTENTICACION) ========
                // CRÍTICO: Permite preflight OPTIONS antes de cualquier otra regla
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // ======== PÚBLICOS (SIN AUTENTICACION) ========
                .requestMatchers("/", "/index.html").permitAll()
                .requestMatchers(HttpMethod.GET, "/").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/medications/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/medications/branch/*/stock").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/medications/branch/*/medications").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/medications/branches").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/medications/*/availability/branch/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/medications/*/availability/branches").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/medications").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/medications/*").permitAll()
                
                // Swagger, actuator, health, error
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-ui.js").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/error").permitAll()

                // Alias legacy deshabilitado explícitamente
                .requestMatchers("/sedes", "/sedes/**").denyAll()

                // ======== AUTHENTICATED (requieren JWT) ========
                .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()

                // ======== ADMIN ONLY ========
                .requestMatchers(HttpMethod.GET, "/api/auth/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/auth/email/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/medications").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/medications/*/branch/*/stock").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/auctions").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/auctions/{id}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/logistics/deliveries/assign").hasAnyRole("ADMIN", "DELIVERY")
                .requestMatchers(HttpMethod.GET, "/api/sedes").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/sedes/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/sedes").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/sedes/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/sedes/*").hasRole("ADMIN")

                // ======== AFFILIATE ONLY ========
                .requestMatchers(HttpMethod.POST, "/api/orders/cart/add").hasRole("AFFILIATE")
                .requestMatchers(HttpMethod.GET, "/api/orders/cart").hasRole("AFFILIATE")
                .requestMatchers(HttpMethod.POST, "/api/orders").hasRole("AFFILIATE")
                .requestMatchers(HttpMethod.POST, "/api/orders/*/confirm").hasRole("AFFILIATE")
                .requestMatchers(HttpMethod.POST, "/api/logistics/orders").hasRole("AFFILIATE")
                .requestMatchers(HttpMethod.POST, "/api/logistics/assignments").hasRole("AFFILIATE")

                // ======== ADMIN O AFFILIATE ========
                .requestMatchers(HttpMethod.GET, "/api/logistics/dashboard").hasAnyRole("ADMIN", "AFFILIATE")
                .requestMatchers("/api/logistics/dashboard/**").hasAnyRole("ADMIN", "AFFILIATE")
                .requestMatchers(HttpMethod.GET, "/api/auctions/won").hasAnyRole("ADMIN", "AFFILIATE")
                .requestMatchers(HttpMethod.GET, "/api/auctions/{id}").hasAnyRole("ADMIN", "AFFILIATE")
                .requestMatchers(HttpMethod.GET, "/api/auctions/active").hasAnyRole("ADMIN", "AFFILIATE")
                .requestMatchers(HttpMethod.GET, "/api/auctions/{id}/bids").hasAnyRole("ADMIN", "AFFILIATE")
                .requestMatchers(HttpMethod.GET, "/api/auctions/{id}/winner").hasAnyRole("ADMIN", "AFFILIATE")
                .requestMatchers(HttpMethod.POST, "/api/auctions/{id}/join").hasAnyRole("ADMIN", "AFFILIATE")
                .requestMatchers(HttpMethod.POST, "/api/auctions/{id}/bids").hasAnyRole("ADMIN", "AFFILIATE")

                // ======== DELIVERY ONLY ========
                .requestMatchers(HttpMethod.PUT, "/api/logistics/deliveries/*/location").hasRole("DELIVERY")
                .requestMatchers(HttpMethod.PUT, "/api/logistics/deliveries/*/complete").hasRole("DELIVERY")
                .requestMatchers(HttpMethod.PUT, "/api/logistics/deliveries/*/pickup").hasRole("DELIVERY")
                .requestMatchers(HttpMethod.GET, "/api/logistics/deliveries/active").hasRole("DELIVERY")
                .requestMatchers(HttpMethod.GET, "/api/logistics/deliveries/*").hasRole("DELIVERY")
                .requestMatchers(HttpMethod.GET, "/api/driver/history/**").hasRole("DELIVERY")
                .requestMatchers(HttpMethod.POST, "/api/driver/support/**").hasRole("DELIVERY")
                .requestMatchers(HttpMethod.GET, "/api/orders").hasAnyRole("DELIVERY", "ADMIN")

                // ======== AFFILIATE + DELIVERY + ADMIN ========
                .requestMatchers(HttpMethod.GET, "/api/logistics/orders/*/status").hasAnyRole("AFFILIATE", "DELIVERY", "ADMIN")

                // ======== GENERIC API (FALLBACK) ========
                // Esta línea permitirá que el Catch-All Controller reciba las peticiones.
                .requestMatchers("/api/**").authenticated()

                // Cualquier otra ruta: requiere autenticación
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuración CORS Global
     * - Soporta múltiples orígenes de Vercel dinámicamente
     * - Permite desarrollo local
     * - Expone headers necesarios para JWT
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Credenciales: false porque usamos JWT en headers, no cookies
        config.setAllowCredentials(false);
        
        // Orígenes permitidos
        config.setAllowedOrigins(Arrays.asList(
            // Desarrollo local
            "http://localhost:5173",      // Vite default
            "http://localhost:3000",       // Next.js, CRA default
            "http://localhost:4200",       // Angular default
            "http://localhost:8080",       // General dev

            // Red local (LAN) — pruebas desde otro dispositivo en el mismo WiFi
            "http://192.168.1.104:5173",  // Frontend desde IP local

            // Producción - Vercel (soporta preview deployments con *.vercel.app)
            "https://frontmedigo.vercel.app",  // Dominio principal de Vercel
            "https://frontmedigo-4r1srb9qh-anderson-fabian-garcia-nietos-projects.vercel.app"  // URL actual
        ));
        
        // NOTA: Si necesitas soportar TODOS los *.vercel.app dinámicamente, considera usar:
        // config.setAllowedOriginPatterns(Arrays.asList("https://*.vercel.app"));
        // Pero esto solo funciona con allowCredentials=false (que ya es nuestro caso)
        
        // Métodos HTTP permitidos
        config.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        
        // Headers permitidos en la request
        config.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Trace-Id",
            "X-Trace-ID",
            "X-Requested-With",
            "X-CSRF-Token",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Headers expuestos en la response (para que JS pueda leerlos)
        config.setExposedHeaders(Arrays.asList(
            "Authorization",
            "X-Trace-ID",
            "X-Content-Type-Options"
        ));
        
        // Tiempo de cache del preflight (1 hora en segundos)
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
