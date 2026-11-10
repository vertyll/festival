package com.vertyll.festival.security;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.savedrequest.SimpleSavedRequest;

import static java.nio.charset.StandardCharsets.UTF_8;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SessionStoreConfigTest {

    private static final String REGISTRATION_ID = "page";
    private static final Instant ISSUED = Instant.parse("2026-11-10T08:00:00Z");
    private static final Instant EXPIRES = ISSUED.plusSeconds(300);

    private final RedisSerializer<Object> serializer = SessionStoreConfig.sessionSerializer();

    @Test
    void theAuthorizedClientWithItsTokensSurvivesTheStore() {
        Map<String, OAuth2AuthorizedClient> clients = new HashMap<>();
        clients.put(REGISTRATION_ID, authorizedClient());

        Object read = serializer.deserialize(serializer.serialize(clients));

        assertThat(read).isInstanceOf(Map.class);
        OAuth2AuthorizedClient client =
                Objects.requireNonNull((OAuth2AuthorizedClient) ((Map<?, ?>) read).get(REGISTRATION_ID));
        assertThat(client.getPrincipalName()).isEqualTo("ola");
        assertThat(client.getClientRegistration().getRegistrationId()).isEqualTo(REGISTRATION_ID);
        assertThat(client.getAccessToken().getTokenValue()).isEqualTo("access-token");
        assertThat(client.getAccessToken().getExpiresAt()).isEqualTo(EXPIRES);
        assertThat(Objects.requireNonNull(client.getRefreshToken()).getTokenValue()).isEqualTo("refresh-token");
    }

    @Test
    void theSecurityContextOfASignInSurvivesTheStore() throws MalformedURLException {
        OidcIdToken idToken = new OidcIdToken(
            "id-token",
            ISSUED,
            EXPIRES,
            Map.of("sub", "ola", "iss", URI.create("https://kc").toURL(), "aud", List.of("page"))
        );
        DefaultOidcUser user = new DefaultOidcUser(List.of(new SimpleGrantedAuthority(Roles.USER_AUTHORITY)), idToken);
        SecurityContextImpl context =
                new SecurityContextImpl(new OAuth2AuthenticationToken(user, user.getAuthorities(), REGISTRATION_ID));

        Object read = serializer.deserialize(serializer.serialize(context));

        assertThat(read).isInstanceOf(SecurityContextImpl.class);
        OAuth2AuthenticationToken authentication =
                (OAuth2AuthenticationToken) Objects.requireNonNull(((SecurityContextImpl) read).getAuthentication());
        assertThat(authentication.getName()).isEqualTo("ola");
        assertThat(authentication.getAuthorities()).extracting(Object::toString).containsExactly(Roles.USER_AUTHORITY);
    }

    @Test
    void thePageASignInReturnsToSurvivesTheStore() {
        Object read = serializer.deserialize(serializer.serialize(new SimpleSavedRequest("/account/orders")));

        assertThat(read).isInstanceOf(SimpleSavedRequest.class);
        assertThat(((SimpleSavedRequest) read).getRedirectUrl()).isEqualTo("/account/orders");
    }

    @Test
    void aStoredValueNamingAnotherClassIsRefused() {
        byte[] stored = "{\"@class\":\"java.lang.ProcessBuilder\",\"command\":[\"true\"]}".getBytes(UTF_8);

        assertThatThrownBy(() -> serializer.deserialize(stored)).isInstanceOf(SerializationException.class);
    }

    private static OAuth2AuthorizedClient authorizedClient() {
        ClientRegistration registration = ClientRegistration.withRegistrationId(REGISTRATION_ID)
            .clientId("festival-page")
            .clientSecret("secret")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("http://localhost:3000/login/oauth2/code/{registrationId}")
            .scope("openid", "email")
            .authorizationUri("https://kc/auth")
            .tokenUri("https://kc/token")
            .jwkSetUri("https://kc/certs")
            .build();
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            "access-token",
            ISSUED,
            EXPIRES,
            Set.of("openid")
        );
        return new OAuth2AuthorizedClient(
            registration,
            "ola",
            accessToken,
            new OAuth2RefreshToken("refresh-token", ISSUED)
        );
    }
}
