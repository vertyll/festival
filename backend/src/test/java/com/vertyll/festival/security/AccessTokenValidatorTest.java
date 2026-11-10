package com.vertyll.festival.security;

import java.time.Instant;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;

import com.vertyll.festival.keycloak.KeycloakProperties;

import static org.assertj.core.api.Assertions.assertThat;

class AccessTokenValidatorTest {

    private static final String REALM = "https://keycloak.example.com/realms/festival";

    private final OAuth2TokenValidator<Jwt> validator =
            SecurityConfig.accessTokenValidator(new KeycloakProperties(REALM, null, "festival-api"));

    @Test
    void anAccessTokenForThisApiIsAccepted() {
        assertThat(validator.validate(token("Bearer", "festival-api")).hasErrors()).isFalse();
    }

    @Test
    void anIdTokenIsRefused() {
        assertThat(validator.validate(token("ID", "festival-api")).hasErrors()).isTrue();
    }

    @Test
    void aTokenThatNamesNoTypeIsRefused() {
        assertThat(validator.validate(token(null, "festival-api")).hasErrors()).isTrue();
    }

    @Test
    void anAccessTokenForAnotherAudienceIsRefused() {
        assertThat(validator.validate(token("Bearer", "festival-page")).hasErrors()).isTrue();
    }

    private static Jwt token(@Nullable String type, String audience) {
        Instant now = Instant.now();
        Jwt.Builder token = Jwt.withTokenValue("token")
            .header("alg", "RS256")
            .issuer(REALM)
            .subject("ola")
            .audience(List.of(audience))
            .issuedAt(now)
            .expiresAt(now.plusSeconds(60));
        if (type != null) {
            token.claim("typ", type);
        }
        return token.build();
    }
}
