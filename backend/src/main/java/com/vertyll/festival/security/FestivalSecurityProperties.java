package com.vertyll.festival.security;

import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("application.security")
public record FestivalSecurityProperties(
    @NotNull Boolean secureCookies,
    @NotNull Map<LoginClient, @Valid @NotNull ClientSettings> login
) {

    public FestivalSecurityProperties {
        Set<LoginClient> missing = EnumSet.allOf(LoginClient.class);
        missing.removeAll(login.keySet());
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Missing application.security.login.* for clients: " + missing);
        }
        login = Map.copyOf(login);
    }

    ClientSettings client(LoginClient client) {
        return Objects.requireNonNull(login.get(client));
    }

    public record ClientSettings(
        @NotBlank String baseUrl,
        @NotBlank String clientId,
        @NotBlank String clientSecret,
        @NotBlank String successPath,
        @NotBlank String failurePath
    ) {
    }
}
