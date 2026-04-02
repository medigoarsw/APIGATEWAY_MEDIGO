package com.medigo.gateway.infrastructure.adapter.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medigo.gateway.application.service.ValidationService;
import com.medigo.gateway.domain.port.in.ForwardingUseCase;
import com.medigo.gateway.domain.port.out.JwtPort;
import com.medigo.gateway.domain.port.out.RateLimitPort;
import com.medigo.gateway.infrastructure.config.GatewayProperties;
import com.medigo.gateway.infrastructure.exception.BackendUnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuctionController.class)
class AuctionControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean ForwardingUseCase forwardingUseCase;
    @MockBean ValidationService validationService;
    @MockBean JwtPort jwtPort;
    @MockBean RateLimitPort rateLimitPort;

    @TestConfiguration
    static class TestBeans {
        @Bean
        GatewayProperties gatewayProperties() {
            return new GatewayProperties();
        }
    }

    // --- GET /api/auctions/{id}/winner ---

    @Test
    @WithMockUser
    void getWinner_returns200_withWinnerBody() throws Exception {
        Map<String, Object> winner = Map.of(
                "auctionId", 1,
                "winnerId", 42,
                "winnerName", "Juan Pérez",
                "winningAmount", 1500
        );
        when(forwardingUseCase.forward(eq("/api/auctions/1/winner"), any(), isNull()))
                .thenReturn(ResponseEntity.ok(winner));

        mockMvc.perform(get("/api/auctions/1/winner")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.winnerId").value(42))
                .andExpect(jsonPath("$.winnerName").value("Juan Pérez"))
                .andExpect(jsonPath("$.winningAmount").value(1500));
    }

    @Test
    @WithMockUser
    void getWinner_returns204_whenNoWinnerYet() throws Exception {
        when(forwardingUseCase.forward(eq("/api/auctions/1/winner"), any(), isNull()))
                .thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(get("/api/auctions/1/winner"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void getWinner_returns404_whenAuctionNotFound() throws Exception {
        when(forwardingUseCase.forward(eq("/api/auctions/99/winner"), any(), isNull()))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/api/auctions/99/winner"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getWinner_returns401_whenBackendRejectsToken() throws Exception {
        when(forwardingUseCase.forward(eq("/api/auctions/1/winner"), any(), isNull()))
                .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());

        mockMvc.perform(get("/api/auctions/1/winner"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getWinner_returns403_whenBackendForbids() throws Exception {
        when(forwardingUseCase.forward(eq("/api/auctions/1/winner"), any(), isNull()))
                .thenReturn(ResponseEntity.status(HttpStatus.FORBIDDEN).build());

        mockMvc.perform(get("/api/auctions/1/winner"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getWinner_returns503_whenUpstreamUnavailable() throws Exception {
        when(forwardingUseCase.forward(eq("/api/auctions/1/winner"), any(), isNull()))
                .thenThrow(new BackendUnavailableException("Backend no disponible temporalmente"));

        mockMvc.perform(get("/api/auctions/1/winner"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    @WithMockUser
    void getWinner_propagatesAuthorizationHeader() throws Exception {
        ArgumentCaptor<HttpServletRequest> reqCaptor = ArgumentCaptor.forClass(HttpServletRequest.class);
        when(forwardingUseCase.forward(any(), reqCaptor.capture(), isNull()))
                .thenReturn(ResponseEntity.ok(Map.of()));

        mockMvc.perform(get("/api/auctions/1/winner")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());

        assertThat(reqCaptor.getValue().getHeader("Authorization")).isEqualTo("Bearer test-token");
    }
}
