package com.vertyll.festival;

import java.time.Instant;
import java.util.Arrays;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Client;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;

public final class TestUsers {

    private TestUsers() {
    }

    public static RequestPostProcessor customer() {
        return user("page", "customer", "klient@example.com", TestAccessTokens.CUSTOMER, "ROLE_USER");
    }

    public static RequestPostProcessor admin() {
        return user("admin", "admin", "admin@example.com", TestAccessTokens.ADMIN, "ROLE_USER", "ROLE_ADMIN");
    }

    public static RequestPostProcessor removedAdmin() {
        return user(
            "admin",
            "removed-admin",
            "removed-admin@example.com",
            TestAccessTokens.CUSTOMER,
            "ROLE_USER",
            "ROLE_ADMIN"
        );
    }

    private static RequestPostProcessor user(
        String registrationId,
        String subject,
        String email,
        String accessToken,
        String... roles
    ) {
        Instant now = Instant.now();
        RequestPostProcessor login = oidcLogin().clientRegistration(registration(registrationId))
            .idToken(token -> token.subject(subject).claim("email", email).claim("email_verified", true))
            .authorities(Arrays.stream(roles).map(SimpleGrantedAuthority::new).toArray(GrantedAuthority[]::new));
        RequestPostProcessor client = oauth2Client(registrationId).clientRegistration(registration(registrationId))
            .principalName(subject)
            .accessToken(
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, accessToken, now, now.plusSeconds(300))
            );
        return request -> client.postProcessRequest(login.postProcessRequest(request));
    }

    private static ClientRegistration registration(String registrationId) {
        return ClientRegistration.withRegistrationId(registrationId)
            .clientId("test-" + registrationId)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("http://localhost/login/oauth2/code/{registrationId}")
            .authorizationUri("http://localhost/auth")
            .tokenUri("http://localhost/token")
            .build();
    }
}
