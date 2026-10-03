package com.vertyll.festival.security;

import jakarta.validation.constraints.NotBlank;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("application.keycloak")
record KeycloakProperties(@NotBlank String realmUrl, @Nullable String backchannelRealmUrl) {

    String backchannel() {
        return backchannelRealmUrl == null || backchannelRealmUrl.isBlank() ? realmUrl : backchannelRealmUrl;
    }

    String endpoint(String path) {
        return realmUrl + "/protocol/openid-connect/" + path;
    }

    String backchannelEndpoint(String path) {
        return backchannel() + "/protocol/openid-connect/" + path;
    }
}
