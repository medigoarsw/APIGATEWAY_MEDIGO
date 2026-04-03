package com.medigo.gateway.domain.model;

import lombok.Builder;
import lombok.Data;

/**
 * Modelo de dominio: claims del JWT de MediGo.
 */
@Data
@Builder
public class UserClaims {
    private Long userId;
    private String username;
    private String email;
    private String role;
}
