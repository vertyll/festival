package com.vertyll.festival.security;

import java.time.Clock;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.oauth2.client.DelegatingOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.RestClientRefreshTokenTokenResponseClient;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenValidator;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import com.vertyll.festival.keycloak.KeycloakAccounts;
import com.vertyll.festival.keycloak.KeycloakHttp;
import com.vertyll.festival.keycloak.KeycloakProperties;

import tools.jackson.databind.ObjectMapper;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
class SecurityConfig {

    /** Keycloak's own type of a token, {@code Bearer} on an access token and {@code ID} on an ID token. */
    private static final String TOKEN_TYPE_CLAIM = "typ";

    private static final String ACCESS_TOKEN_TYPE = "Bearer";

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

    // The one place that assembles the filter chain takes every collaborator a filter in it needs.
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Bean
    SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        FestivalOidcUserService oidcUserService,
        SessionAccessTokens sessionAccessTokens,
        LoginRedirectHandler loginRedirectHandler,
        ClientRegistrationRepository clientRegistrations,
        OAuth2AuthorizedClientRepository authorizedClients,
        KeycloakProperties keycloak,
        KeycloakAccounts keycloakAccounts,
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
            .oauth2ResourceServer(
                resourceServer -> resourceServer.jwt(
                    jwt -> jwt.jwtAuthenticationConverter(
                        token -> new JwtAuthenticationToken(token, Roles.authorities(token))
                    )
                )
            )
            .csrf(
                csrf -> csrf.spa()
                    .csrfTokenRepository(csrfTokenRepository(properties.secureCookies()))
                    .ignoringRequestMatchers(new BearerTokenRequests())
            )
            .exceptionHandling(
                exceptions -> exceptions.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .requestCache(RequestCacheConfigurer::disable)
            .addFilterBefore(new SessionAccessTokenFilter(sessionAccessTokens), AnonymousAuthenticationFilter.class)
            .addFilterBefore(
                new KeycloakLanguageFilter(sessionAccessTokens, keycloakAccounts),
                OAuth2AuthorizationRequestRedirectFilter.class
            );
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

    /** The code exchange of a sign-in, which {@code oauth2Login} takes from the context by its type. */
    @Bean
    OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authorizationCodeTokenResponseClient(
        KeycloakHttp keycloakHttp
    ) {
        RestClientAuthorizationCodeTokenResponseClient client = new RestClientAuthorizationCodeTokenResponseClient();
        client.setRestClient(tokenEndpointClient(keycloakHttp.requestFactory()));
        return client;
    }

    @Bean
    OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> refreshTokenResponseClient(
        KeycloakHttp keycloakHttp
    ) {
        RestClientRefreshTokenTokenResponseClient client = new RestClientRefreshTokenTokenResponseClient();
        client.setRestClient(tokenEndpointClient(keycloakHttp.requestFactory()));
        return client;
    }

    /**
     * The ID-token decoder of a sign-in, which {@code oauth2Login} takes from the context by its type: what
     * {@link OidcIdTokenDecoderFactory} builds, the timestamps and the OpenID Connect claims checked and the claim
     * types converted, with the key set fetched through the client with Keycloak's timeouts, which that factory has no
     * way to take.
     */
    @Bean
    JwtDecoderFactory<ClientRegistration> idTokenDecoderFactory(KeycloakHttp keycloakHttp) {
        RestTemplate keySet = new RestTemplate(keycloakHttp.requestFactory());
        Map<String, JwtDecoder> decoders = new ConcurrentHashMap<>();
        return registration -> decoders.computeIfAbsent(registration.getRegistrationId(), _ -> {
            NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withJwkSetUri(
                    Objects
                        .requireNonNull(registration.getProviderDetails().getJwkSetUri(), "No key set for the client")
                )
                .restOperations(keySet)
                .build();
            decoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<>(
                    new JwtTimestampValidator(),
                    new OidcIdTokenValidator(registration)
                )
            );
            decoder.setClaimSetConverter(OidcIdTokenDecoderFactory.createDefaultClaimTypeConverter());
            return decoder;
        });
    }

    @Bean
    OAuth2AuthorizedClientManager authorizedClientManager(
        ClientRegistrationRepository clientRegistrations,
        OAuth2AuthorizedClientRepository authorizedClients,
        OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> refreshTokenResponseClient,
        SharedRefreshes sharedRefreshes
    ) {
        DefaultOAuth2AuthorizedClientManager manager =
                new DefaultOAuth2AuthorizedClientManager(clientRegistrations, authorizedClients);
        manager.setAuthorizedClientProvider(
            new DelegatingOAuth2AuthorizedClientProvider(
                OAuth2AuthorizedClientProviderBuilder.builder().authorizationCode().build(),
                new SingleFlightRefreshTokenProvider(
                    OAuth2AuthorizedClientProviderBuilder.builder()
                        .refreshToken(refresh -> refresh.accessTokenResponseClient(refreshTokenResponseClient))
                        .build(),
                    sharedRefreshes,
                    Clock.systemUTC()
                )
            )
        );
        return manager;
    }

    /**
     * Access tokens: Keycloak's signature, issuer and expiry, this API as the audience, and Keycloak's own {@code typ}
     * claim, which refuses an ID token signed with the same keys, as the realm issues no RFC 9068 {@code at+jwt}
     * header.
     */
    @Bean
    JwtDecoder accessTokenDecoder(KeycloakProperties keycloak, KeycloakHttp keycloakHttp) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(keycloak.backchannelEndpoint("certs"))
            .restOperations(new RestTemplate(keycloakHttp.requestFactory()))
            .build();
        decoder.setJwtValidator(accessTokenValidator(keycloak));
        return decoder;
    }

    static OAuth2TokenValidator<Jwt> accessTokenValidator(KeycloakProperties keycloak) {
        return new DelegatingOAuth2TokenValidator<>(
            JwtValidators.createDefaultWithIssuer(keycloak.realmUrl()),
            new JwtClaimValidator<List<String>>(
                JwtClaimNames.AUD,
                audience -> audience != null && audience.contains(keycloak.audience())
            ),
            new JwtClaimValidator<>(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE::equals)
        );
    }

    /**
     * The token endpoint's client as Spring Security's own default builds it, its form and token-response converters
     * and its OAuth2 error handler, on Keycloak's timed requests.
     */
    private static RestClient tokenEndpointClient(JdkClientHttpRequestFactory requests) {
        return RestClient.builder()
            .requestFactory(requests)
            .configureMessageConverters(
                converters -> converters.addCustomConverter(new FormHttpMessageConverter())
                    .addCustomConverter(new OAuth2AccessTokenResponseHttpMessageConverter())
            )
            .defaultStatusHandler(new OAuth2ErrorResponseErrorHandler())
            .build();
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
