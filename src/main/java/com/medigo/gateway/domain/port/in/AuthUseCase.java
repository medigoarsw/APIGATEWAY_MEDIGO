package com.medigo.gateway.domain.port.in;

import com.medigo.gateway.application.dto.request.LoginRequest;
import com.medigo.gateway.application.dto.request.RegisterRequest;
import com.medigo.gateway.application.dto.response.LoginResponse;
import com.medigo.gateway.application.dto.response.RegisterResponse;
import com.medigo.gateway.application.dto.response.UserResponseDto;

/**
 * Puerto de entrada: caso de uso de autenticación.
 */
public interface AuthUseCase {
    LoginResponse login(LoginRequest request);
    
    RegisterResponse register(RegisterRequest request);
    
    UserResponseDto getMe(Long userId);
    
    UserResponseDto getUserById(Long id);
    
    UserResponseDto getUserByEmail(String email);
}
