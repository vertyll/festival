package com.vertyll.festival.security;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("festival.security")
public record FestivalSecurityProperties(
    @NotNull List<@NotBlank @Email String> bootstrapAdmins,
    @NotNull Boolean secureCookies,
    @NotNull Map<LoginClient, @Valid @NotNull LoginRedirects> login
) {

    public FestivalSecurityProperties {
        bootstrapAdmins = List.copyOf(bootstrapAdmins);
        Set<LoginClient> missing = EnumSet.allOf(LoginClient.class);
        missing.removeAll(login.keySet());
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Missing festival.security.login.* for clients: " + missing);
        }
        login = Map.copyOf(login);
    }

    LoginRedirects redirects(LoginClient client) {
        return Objects.requireNonNull(login.get(client));
    }

    public record LoginRedirects(@NotBlank String successPath, @NotBlank String failurePath) {
    }
}
