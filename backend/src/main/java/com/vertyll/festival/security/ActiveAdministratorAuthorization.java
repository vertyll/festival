package com.vertyll.festival.security;

import java.util.function.Supplier;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
class ActiveAdministratorAuthorization implements AuthorizationManager<RequestAuthorizationContext> {

    private final OAuth2AuthorizedClientManager authorizedClients;
    private final JwtDecoder accessTokens;

    @Override
    public AuthorizationResult authorize(
        Supplier<? extends @Nullable Authentication> authentication,
        RequestAuthorizationContext context
    ) {
        return new AuthorizationDecision(isActiveAdministrator(authentication.get()));
    }

    boolean isActiveAdministrator(@Nullable Authentication authentication) {
        if (!(authentication instanceof OAuth2AuthenticationToken token)
                || UserIdentity.from(token).filter(UserIdentity::administrator).isEmpty()) {
            return false;
        }
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)
                || attributes.getResponse() == null) {
            return false;
        }
        try {
            OAuth2AuthorizedClient client = authorizedClients.authorize(
                OAuth2AuthorizeRequest.withClientRegistrationId(token.getAuthorizedClientRegistrationId())
                    .principal(token)
                    .attribute(HttpServletRequest.class.getName(), attributes.getRequest())
                    .attribute(HttpServletResponse.class.getName(), attributes.getResponse())
                    .build()
            );
            return client != null
                    && Roles.isAdministrator(accessTokens.decode(client.getAccessToken().getTokenValue()));
        } catch (ClientAuthorizationException | JwtException e) {
            log.info("[INFO] Administrator access withdrawn: {}", e.getClass().getSimpleName());
            return false;
        }
    }
}
