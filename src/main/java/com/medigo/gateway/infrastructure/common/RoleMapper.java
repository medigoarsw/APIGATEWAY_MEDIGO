package com.medigo.gateway.infrastructure.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Mapea roles del backend a roles canonicos esperados por SecurityConfig.
 *
 * Conversiones:
 * - USUARIO -> AFFILIATE
 * - REPARTIDOR -> DELIVERY
 * - ADMIN -> ADMIN (sin cambios)
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleMapper {

    /**
     * Convierte un rol del backend a rol canonico.
     * @param backendRole rol recibido del backend
     * @return rol canonico (ADMIN, AFFILIATE, DELIVERY)
     */
    public static String toCanonical(String backendRole) {
        if (backendRole == null || backendRole.isBlank()) {
            log.warn("Rol vacio del backend, asumiendo AFFILIATE por defecto");
            return "AFFILIATE";
        }

        String normalized = backendRole.trim().toUpperCase();

        return switch (normalized) {
            case "USUARIO" -> "AFFILIATE";
            case "REPARTIDOR" -> "DELIVERY";
            case "ADMIN" -> "ADMIN";
            case "AFFILIATE" -> "AFFILIATE";
            case "DELIVERY" -> "DELIVERY";
            default -> {
                log.warn("Rol desconocido del backend: '{}', mapeando a AFFILIATE por defecto", backendRole);
                yield "AFFILIATE";
            }
        };
    }
}
