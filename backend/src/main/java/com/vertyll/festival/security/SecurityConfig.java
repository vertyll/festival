package com.vertyll.festival.security;

import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import static java.util.Objects.requireNonNull;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
class SecurityConfig {

    private static final String[] PUBLIC_READ_ENDPOINTS = {
        "/api/me",
        "/api/settings",
        "/api/products",
        "/api/products/*",
        "/api/artists",
        "/api/artists/*",
        "/api/news",
        "/api/news/*",
        "/api/sponsors",
        "/api/i18n/*"
    };

    @Bean
    SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        FestivalOidcUserService oidcUserService,
        ActiveAdministratorAuthorization activeAdministrator,
        LoginRedirectHandler loginRedirectHandler,
        FestivalSecurityProperties properties,
        ServerProperties serverProperties
    ) {
        http.authorizeHttpRequests(
            authorize -> authorize.requestMatchers(HttpMethod.GET, PUBLIC_READ_ENDPOINTS)
                .permitAll()
                .requestMatchers("/api/admin/**")
                .access(activeAdministrator)
                .requestMatchers("/api/account/**")
                .authenticated()
                .requestMatchers(HttpMethod.POST, "/api/orders")
                .authenticated()
                .requestMatchers("/actuator/health", "/actuator/health/**", "/error")
                .permitAll()
                .anyRequest()
                .denyAll()
        )
            .oauth2Login(
                login -> disableGeneratedLoginPage(login)
                    .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService))
                    .successHandler(loginRedirectHandler)
                    .failureHandler(loginRedirectHandler)
            )
            .logout(
                logout -> logout.logoutUrl("/logout")
                    .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
                    .deleteCookies(sessionCookieName(serverProperties))
            )
            .csrf(csrf -> csrf.spa().csrfTokenRepository(csrfTokenRepository(properties.secureCookies())))
            .exceptionHandling(
                exceptions -> exceptions.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .requestCache(RequestCacheConfigurer::disable);
        return http.build();
    }

    private static OAuth2LoginConfigurer<HttpSecurity> disableGeneratedLoginPage(
        OAuth2LoginConfigurer<HttpSecurity> login
    ) {
        return login.loginPage("/login");
    }

    private static CookieCsrfTokenRepository csrfTokenRepository(boolean secureCookies) {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieCustomizer(cookie -> cookie.secure(secureCookies).sameSite("Lax"));
        return repository;
    }

    private static String sessionCookieName(ServerProperties serverProperties) {
        return requireNonNull(serverProperties.getServlet().getSession().getCookie().getName());
    }
}
