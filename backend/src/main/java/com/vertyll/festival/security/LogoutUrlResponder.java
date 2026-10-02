package com.vertyll.festival.security;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import tools.jackson.databind.ObjectMapper;

final class LogoutUrlResponder implements LogoutSuccessHandler {

    private final KeycloakProperties keycloak;
    private final FestivalSecurityProperties properties;
    private final ObjectMapper objectMapper;

    LogoutUrlResponder(KeycloakProperties keycloak, FestivalSecurityProperties properties, ObjectMapper objectMapper) {
        this.keycloak = keycloak;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onLogoutSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        @Nullable Authentication authentication
    ) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), Map.of("logoutUrl", logoutUrl(authentication)));
    }

    private String logoutUrl(@Nullable Authentication authentication) {
        if (!(authentication instanceof OAuth2AuthenticationToken token)
                || !(token.getPrincipal() instanceof OidcUser user)) {
            return "/";
        }
        FestivalSecurityProperties.ClientSettings client =
                properties.client(LoginClient.of(token.getAuthorizedClientRegistrationId()));
        return UriComponentsBuilder.fromUriString(keycloak.endpoint("logout"))
            .queryParam("client_id", client.clientId())
            .queryParam("id_token_hint", user.getIdToken().getTokenValue())
            .queryParam("post_logout_redirect_uri", client.baseUrl() + "/")
            .encode()
            .build()
            .toUriString();
    }
}
