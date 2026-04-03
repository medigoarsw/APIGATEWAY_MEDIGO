error id: file:///D:/ander/Documents/SEMESTRE%207/ARSW/PROYECTO%20OFICIAL/APIGATEWAY_MEDIGO/src/main/java/com/medigo/gateway/infrastructure/adapter/out/JjwtAdapter.java:_empty_/Component#
file:///D:/ander/Documents/SEMESTRE%207/ARSW/PROYECTO%20OFICIAL/APIGATEWAY_MEDIGO/src/main/java/com/medigo/gateway/infrastructure/adapter/out/JjwtAdapter.java
empty definition using pc, found symbol in pc: _empty_/Component#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 581
uri: file:///D:/ander/Documents/SEMESTRE%207/ARSW/PROYECTO%20OFICIAL/APIGATEWAY_MEDIGO/src/main/java/com/medigo/gateway/infrastructure/adapter/out/JjwtAdapter.java
text:
```scala
package com.medigo.gateway.infrastructure.adapter.out;

import com.medigo.gateway.domain.model.UserClaims;
import com.medigo.gateway.domain.port.out.JwtPort;
import com.medigo.gateway.infrastructure.config.GatewayProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Adaptador JWT usando JJWT 0.12.x.
 */
@Slf4j
@@@Component
@RequiredArgsConstructor
public class JjwtAdapter implements JwtPort {

    private final GatewayProperties properties;

    @Override
    public String generateToken(UserClaims claims) {
        return Jwts.builder()
                .subject(String.valueOf(claims.getUserId()))
                .claim("username", claims.getUsername())
                .claim("email", claims.getEmail())
                .claim("role", claims.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() +
                        properties.getJwt().getExpirationMs()))
                .signWith(getKey())
                .compact();
    }

    @Override
    public UserClaims validateAndExtract(String token) {
        Claims c = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return UserClaims.builder()
                .userId(Long.parseLong(c.getSubject()))
                .username(c.get("username", String.class))
                .email(c.get("email", String.class))
                .role(c.get("role", String.class))
                .build();
    }

    @Override
    public boolean isValid(String token) {
        try {
            validateAndExtract(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("JWT invalido: {}", ex.getMessage());
            return false;
        }
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(
                properties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8));
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/Component#