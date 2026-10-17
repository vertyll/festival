package com.vertyll.festival.security;

import java.io.IOException;
import java.net.InetAddress;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakLanguageFilterTest {

    @Test
    void aCookieSharedWithKeycloakLivesUnderTheParentDomain() {
        assertThat(KeycloakLanguageFilter.sharedDomain("festival.vertyll.dev")).contains("vertyll.dev");
    }

    @Test
    void localhostAndAnAddressShareCookiesWithoutADomain() {
        assertThat(KeycloakLanguageFilter.sharedDomain("localhost")).isEmpty();
        assertThat(KeycloakLanguageFilter.sharedDomain(InetAddress.getLoopbackAddress().getHostAddress())).isEmpty();
    }

    @Test
    void theHandedBackLanguageIsRemovedFromKeycloaksDomainBehindAProxy() throws ServletException, IOException {
        KeycloakLanguageFilter filter = new KeycloakLanguageFilter(
            "festival",
            "vertyll.dev",
            new SessionAccessTokens(request -> null, token -> {
                throw new IllegalStateException("no token is decoded on the way back");
            }),
            new KeycloakAccount(RestClient.builder())
        );
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/login/oauth2/code/page");
        request.setServerName("festival-backend");
        request.setCookies(new Cookie("ui_locale_festival", "en"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getHeaders("Set-Cookie"))
            .anySatisfy(cookie -> assertThat(cookie).startsWith("ui_locale_festival=;").contains("Domain=vertyll.dev"));
    }
}
