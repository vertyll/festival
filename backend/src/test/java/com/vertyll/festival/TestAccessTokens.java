package com.vertyll.festival;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@TestConfiguration(proxyBeanMethods = false)
public class TestAccessTokens {

    public static final String ADMIN = "admin-access-token";
    public static final String CUSTOMER = "customer-access-token";

    @Bean
    @Primary
    JwtDecoder testAccessTokenDecoder() {
        return token -> switch (token) {
            case ADMIN -> jwt(token, "admin@example.com", List.of("USER", "ADMIN"));
            case CUSTOMER -> jwt(token, "klient@example.com", List.of("USER"));
            default -> throw new BadJwtException("Unknown test token");
        };
    }

    private static Jwt jwt(String token, String email, List<String> roles) {
        Instant now = Instant.now();
        return Jwt.withTokenValue(token)
            .header("alg", "none")
            .subject("test")
            .claim("email", email)
            .claim("realm_access", Map.of("roles", roles))
            .issuedAt(now)
            .expiresAt(now.plusSeconds(300))
            .build();
    }
}
