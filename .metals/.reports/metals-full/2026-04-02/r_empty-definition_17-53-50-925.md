error id: file:///D:/ander/Documents/SEMESTRE%207/ARSW/PROYECTO%20OFICIAL/APIGATEWAY_MEDIGO/src/test/java/com/medigo/gateway/infrastructure/adapter/in/AuthControllerTest.java:org/springframework/boot/test/mock/mockito/MockBean#
file:///D:/ander/Documents/SEMESTRE%207/ARSW/PROYECTO%20OFICIAL/APIGATEWAY_MEDIGO/src/test/java/com/medigo/gateway/infrastructure/adapter/in/AuthControllerTest.java
empty definition using pc, found symbol in pc: org/springframework/boot/test/mock/mockito/MockBean#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 856
uri: file:///D:/ander/Documents/SEMESTRE%207/ARSW/PROYECTO%20OFICIAL/APIGATEWAY_MEDIGO/src/test/java/com/medigo/gateway/infrastructure/adapter/in/AuthControllerTest.java
text:
```scala
package com.medigo.gateway.infrastructure.adapter.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medigo.gateway.application.dto.response.LoginResponse;
import com.medigo.gateway.domain.port.in.AuthUseCase;
import com.medigo.gateway.domain.port.out.JwtPort;
import com.medigo.gateway.domain.port.out.RateLimitPort;
import com.medigo.gateway.infrastructure.config.GatewayProperties;
import com.medigo.gateway.infrastructure.interceptor.AuditLoggingInterceptor;
import com.medigo.gateway.infrastructure.interceptor.RateLimitInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.@@MockBean;
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

        @Bean
        RateLimitInterceptor rateLimitInterceptor() {
            return new RateLimitInterceptor(null, null);
        }

        @Bean
        AuditLoggingInterceptor auditLoggingInterceptor() {
            return new AuditLoggingInterceptor(null);
        }
    }

    @Test
    @WithMockUser
    void testLoginSuccess() throws Exception {
        LoginResponse resp = LoginResponse.builder()
                .id(1L).username("user").email("u@test.com")
                .role("USUARIO").jwtToken("jwt.token.here").build();

        when(authUseCase.login(any())).thenReturn(resp);

        Map<String, String> body = Map.of("username", "user", "password", "password123");

        mockMvc.perform(post("/api/auth/login")
            .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.jwtToken").value("jwt.token.here"));
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

```


#### Short summary: 

empty definition using pc, found symbol in pc: org/springframework/boot/test/mock/mockito/MockBean#