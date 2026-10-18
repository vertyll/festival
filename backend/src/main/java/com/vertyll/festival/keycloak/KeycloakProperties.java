package com.vertyll.festival.keycloak;

import jakarta.validation.constraints.NotBlank;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** Where Keycloak is, for the security set-up and for the account API alike. */
@Validated
@ConfigurationProperties("application.keycloak")
public record KeycloakProperties(
    @NotBlank String realmUrl,
    @Nullable String backchannelRealmUrl,
    @NotBlank String audience
) {

    /**
     * The realm's address as the application reaches it from inside the cluster, which may differ from the browser's.
     */
    public String backchannel() {
        return backchannelRealmUrl == null || backchannelRealmUrl.isBlank() ? realmUrl : backchannelRealmUrl;
    }

    /** An OpenID Connect endpoint of the realm, at the browser's address. */
    public String endpoint(String path) {
        return realmUrl + "/protocol/openid-connect/" + path;
    }

    public String backchannelEndpoint(String path) {
        return backchannel() + "/protocol/openid-connect/" + path;
    }
}
