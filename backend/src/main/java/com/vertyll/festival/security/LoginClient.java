package com.vertyll.festival.security;

import java.util.Arrays;
import java.util.Optional;

enum LoginClient {
    PAGE("page", false),
    ADMIN("admin", true);

    private final String registrationId;
    private final boolean requiresAdministrator;

    LoginClient(String registrationId, boolean requiresAdministrator) {
        this.registrationId = registrationId;
        this.requiresAdministrator = requiresAdministrator;
    }

    String registrationId() {
        return registrationId;
    }

    boolean requiresAdministrator() {
        return requiresAdministrator;
    }

    static Optional<LoginClient> find(String registrationId) {
        return Arrays.stream(values()).filter(client -> client.registrationId.equals(registrationId)).findFirst();
    }

    static LoginClient of(String registrationId) {
        return find(registrationId)
            .orElseThrow(() -> new IllegalStateException("Unknown OAuth2 registration: " + registrationId));
    }
}
