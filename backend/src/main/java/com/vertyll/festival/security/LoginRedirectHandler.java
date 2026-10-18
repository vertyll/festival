package com.vertyll.festival.security;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
class LoginRedirectHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    private static final String CALLBACK_PREFIX = "/login/oauth2/code/";

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final RequestCache requestCache = ReturnPage.requestCache();
    private final FestivalSecurityProperties properties;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {
        if (!(authentication instanceof OAuth2AuthenticationToken token)) {
            throw new IllegalStateException("Expected OAuth2 authentication, got " + authentication.getClass());
        }
        LoginClient client = LoginClient.of(token.getAuthorizedClientRegistrationId());
        // Spring Security's saved request, the page ReturnPage kept, or the client's landing path; the page is used once.
        SavedRequestAwareAuthenticationSuccessHandler landing = new SavedRequestAwareAuthenticationSuccessHandler();
        landing.setRequestCache(requestCache);
        landing.setDefaultTargetUrl(properties.client(client).successPath());
        landing.onAuthenticationSuccess(request, response, authentication);
        requestCache.removeRequest(request, response);
    }

    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException exception
    ) throws IOException {
        log.info("[INFO] OAuth2 login failed: {}", exception.getClass().getSimpleName());
        Optional<LoginClient> client = callbackClient(request);
        if (client.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        redirectStrategy.sendRedirect(request, response, properties.client(client.get()).failurePath());
    }

    private static Optional<LoginClient> callbackClient(HttpServletRequest request) {
        String uri = request.getRequestURI();
        int start = uri.indexOf(CALLBACK_PREFIX);
        return start < 0 ? Optional.empty() : LoginClient.find(uri.substring(start + CALLBACK_PREFIX.length()));
    }
}
