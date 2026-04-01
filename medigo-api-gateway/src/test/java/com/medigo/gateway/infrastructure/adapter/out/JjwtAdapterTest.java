package com.medigo.gateway.infrastructure.adapter.out;

import com.medigo.gateway.domain.model.UserClaims;
import com.medigo.gateway.infrastructure.config.GatewayProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JjwtAdapterTest {

    private JjwtAdapter adapter;

    @BeforeEach
    void setUp() {
        GatewayProperties props = new GatewayProperties();
        props.getJwt().setSecret(
                "medigo-super-secret-key-must-be-at-least-256-bits-long-for-test");
        props.getJwt().setExpirationMs(86_400_000L);
        adapter = new JjwtAdapter(props);
    }

    @Test
    void testGenerateAndValidateToken() {
        UserClaims claims = UserClaims.builder()
                .userId("42").username("alice")
                .email("alice@medigo.com").role("ADMIN").build();

        String token = adapter.generateToken(claims);
        assertThat(token).isNotBlank();
        assertThat(adapter.isValid(token)).isTrue();
    }

    @Test
    void testExtractClaimsFromToken() {
        UserClaims original = UserClaims.builder()
                .userId("7").username("bob")
                .email("bob@medigo.com").role("USUARIO").build();

        String token = adapter.generateToken(original);
        UserClaims extracted = adapter.validateAndExtract(token);

        assertThat(extracted.getUserId()).isEqualTo("7");
        assertThat(extracted.getRole()).isEqualTo("USUARIO");
    }

    @Test
    void testInvalidTokenReturnsFalse() {
        assertThat(adapter.isValid("invalid.token.here")).isFalse();
    }
}
