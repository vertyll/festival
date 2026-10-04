package com.vertyll.festival.security;

import java.time.Clock;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.oauth2.client.DelegatingOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import tools.jackson.databind.ObjectMapper;

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
        SessionAccessTokens sessionAccessTokens,
        LoginRedirectHandler loginRedirectHandler,
        ClientRegistrationRepository clientRegistrations,
        OAuth2AuthorizedClientRepository authorizedClients,
        KeycloakProperties keycloak,
        FestivalSecurityProperties properties,
        ObjectMapper objectMapper
    ) {
        http.authorizeHttpRequests(
            authorize -> authorize.requestMatchers(HttpMethod.GET, PUBLIC_READ_ENDPOINTS)
                .permitAll()
                .requestMatchers("/api/admin/**")
                .hasRole(Roles.ADMIN)
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
                login -> login.loginPage("/login")
                    .authorizationEndpoint(
                        endpoint -> endpoint.authorizationRequestResolver(
                            new LocalizedAuthorizationRequestResolver(clientRegistrations)
                        )
                    )
                    .authorizedClientRepository(authorizedClients)
                    .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService))
                    .successHandler(loginRedirectHandler)
                    .failureHandler(loginRedirectHandler)
            )
            .logout(
                logout -> logout.logoutUrl("/logout")
                    .logoutSuccessHandler(new LogoutUrlResponder(keycloak, properties, objectMapper))
            )
            .csrf(csrf -> csrf.spa().csrfTokenRepository(csrfTokenRepository(properties.secureCookies())))
            .exceptionHandling(
                exceptions -> exceptions.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .requestCache(RequestCacheConfigurer::disable)
            .addFilterBefore(new SessionAccessTokenFilter(sessionAccessTokens), AnonymousAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    ClientRegistrationRepository clientRegistrationRepository(
        KeycloakProperties keycloak,
        FestivalSecurityProperties properties
    ) {
        return new InMemoryClientRegistrationRepository(
            Arrays.stream(LoginClient.values()).map(client -> registration(client, keycloak, properties)).toList()
        );
    }

    @Bean
    OAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new HttpSessionOAuth2AuthorizedClientRepository();
    }

    @Bean
    OAuth2AuthorizedClientManager authorizedClientManager(
        ClientRegistrationRepository clientRegistrations,
        OAuth2AuthorizedClientRepository authorizedClients
    ) {
        DefaultOAuth2AuthorizedClientManager manager =
                new DefaultOAuth2AuthorizedClientManager(clientRegistrations, authorizedClients);
        manager.setAuthorizedClientProvider(
            new DelegatingOAuth2AuthorizedClientProvider(
                OAuth2AuthorizedClientProviderBuilder.builder().authorizationCode().build(),
                new SingleFlightRefreshTokenProvider(
                    OAuth2AuthorizedClientProviderBuilder.builder().refreshToken().build(),
                    Clock.systemUTC()
                )
            )
        );
        return manager;
    }

    @Bean
    JwtDecoder accessTokenDecoder(KeycloakProperties keycloak) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(keycloak.backchannelEndpoint("certs")).build();
        decoder.setJwtValidator(
            new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(keycloak.realmUrl()),
                new JwtClaimValidator<List<String>>(
                    JwtClaimNames.AUD,
                    audience -> audience != null && audience.contains(keycloak.audience())
                )
            )
        );
        return decoder;
    }

    private static ClientRegistration registration(
        LoginClient client,
        KeycloakProperties keycloak,
        FestivalSecurityProperties properties
    ) {
        FestivalSecurityProperties.ClientSettings settings = properties.client(client);
        return ClientRegistration.withRegistrationId(client.registrationId())
            .clientName("Keycloak")
            .clientId(settings.clientId())
            .clientSecret(settings.clientSecret())
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri(settings.baseUrl() + "/login/oauth2/code/{registrationId}")
            .scope(OidcScopes.OPENID, OidcScopes.PROFILE, OidcScopes.EMAIL)
            .authorizationUri(keycloak.endpoint("auth"))
            .tokenUri(keycloak.backchannelEndpoint("token"))
            .jwkSetUri(keycloak.backchannelEndpoint("certs"))
            .issuerUri(keycloak.realmUrl())
            .userNameAttributeName(IdTokenClaimNames.SUB)
            .build();
    }

    @SuppressWarnings("java:S3330")
    private static CookieCsrfTokenRepository csrfTokenRepository(boolean secureCookies) {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieCustomizer(cookie -> cookie.secure(secureCookies).sameSite("Lax"));
        return repository;
    }
}
