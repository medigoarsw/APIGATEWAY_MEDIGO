package com.medigo.gateway.infrastructure.adapter.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medigo.gateway.application.dto.response.LoginResponse;
import com.medigo.gateway.domain.port.in.AuthUseCase;
import com.medigo.gateway.domain.port.out.JwtPort;
import com.medigo.gateway.domain.port.out.RateLimitPort;
import com.medigo.gateway.infrastructure.config.GatewayProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean AuthUseCase authUseCase;
    @MockBean JwtPort jwtPort;
    @MockBean RateLimitPort rateLimitPort;

    @TestConfiguration
    static class TestBeans {
        @Bean
        GatewayProperties gatewayProperties() {
            return new GatewayProperties();
        }
    }

    @Test
    @WithMockUser
    void testLoginInvalidRequest() throws Exception {
        Map<String, String> body = Map.of("username", "", "password", "");

        mockMvc.perform(post("/api/auth/login")
            .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }
}
