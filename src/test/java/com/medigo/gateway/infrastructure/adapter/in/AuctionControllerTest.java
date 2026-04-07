package com.medigo.gateway.infrastructure.adapter.in;

import com.medigo.gateway.application.service.ValidationService;
import com.medigo.gateway.domain.port.in.ForwardingUseCase;
import com.medigo.gateway.domain.port.out.JwtPort;
import com.medigo.gateway.domain.port.out.RateLimitPort;
import com.medigo.gateway.infrastructure.config.GatewayProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuctionController.class)
class AuctionControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean ForwardingUseCase forwardingUseCase;
    @MockBean JwtPort jwtPort;
    @MockBean RateLimitPort rateLimitPort;

        @BeforeEach
        void allowRateLimitInTests() {
                when(rateLimitPort.isAllowed(anyString(), anyInt())).thenReturn(true);
        }

    @TestConfiguration
    static class TestBeans {
        @Bean
        GatewayProperties gatewayProperties() {
            return new GatewayProperties();
        }

        @Bean
        ValidationService validationService() {
            return new ValidationService();
        }
    }

    @Test
    @WithMockUser(roles = "AFFILIATE")
    void wonAuctions_affiliateAuthenticated_returns200() throws Exception {
        when(forwardingUseCase.forward(eq("/api/auctions/won?page=0&size=20"), any(), eq(null)))
                .thenReturn(ResponseEntity.ok(Map.of("content", List.of())));

        mockMvc.perform(get("/api/auctions/won"))
                .andExpect(status().isOk());
    }

    @Test
    void wonAuctions_unauthenticated_returns401Or403() throws Exception {
        mockMvc.perform(get("/api/auctions/won"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertTrue(status == 401 || status == 403);
                });
    }

    @Test
    @WithMockUser(roles = "AFFILIATE")
    void wonAuctions_pageAndSize_arePropagated() throws Exception {
        when(forwardingUseCase.forward(eq("/api/auctions/won?page=2&size=5"), any(), eq(null)))
                .thenReturn(ResponseEntity.ok(Map.of("content", List.of())));

        mockMvc.perform(get("/api/auctions/won")
                        .param("page", "2")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(forwardingUseCase).forward(eq("/api/auctions/won?page=2&size=5"), any(), eq(null));
    }

    @Test
    @WithMockUser(roles = "AFFILIATE")
    void wonAuctions_emptyResult_returns200WithPaginatedStructure() throws Exception {
        Map<String, Object> body = Map.of(
                "content", List.of(),
                "page", 0,
                "size", 20,
                "totalElements", 0,
                "totalPages", 0,
                "links", Map.of("self", "/api/auctions/won?page=0&size=20")
        );

        when(forwardingUseCase.forward(eq("/api/auctions/won?page=0&size=20"), any(), eq(null)))
                .thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(get("/api/auctions/won"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.links.self").value("/api/auctions/won?page=0&size=20"));
    }

    @Test
    @WithMockUser(roles = "AFFILIATE")
    void wonAuctions_ignoresClientUserIdOverride() throws Exception {
        when(forwardingUseCase.forward(eq("/api/auctions/won?page=0&size=20"), any(), eq(null)))
                .thenReturn(ResponseEntity.ok(Map.of("content", List.of())));

        mockMvc.perform(get("/api/auctions/won")
                        .param("userId", "999"))
                .andExpect(status().isOk());

        verify(forwardingUseCase).forward(eq("/api/auctions/won?page=0&size=20"), any(), eq(null));
    }
}
