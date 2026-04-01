package com.medigo.gateway.domain.port.in;

import com.medigo.gateway.application.dto.request.LoginRequest;
import com.medigo.gateway.application.dto.response.LoginResponse;

/**
 * Puerto de entrada: caso de uso de autenticación.
 */
public interface AuthUseCase {
    LoginResponse login(LoginRequest request);
}
