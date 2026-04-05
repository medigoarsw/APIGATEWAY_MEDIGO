package com.medigo.gateway.domain.port.out;

import com.medigo.gateway.domain.model.UserClaims;

/**
 * Puerto de salida: operaciones con tokens JWT.
 */
public interface JwtPort {
    String generateToken(UserClaims claims);
    UserClaims validateAndExtract(String token);
    boolean isValid(String token);
}
