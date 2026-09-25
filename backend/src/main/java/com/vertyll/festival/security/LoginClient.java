package com.vertyll.festival.security;

import java.util.Arrays;
import java.util.Optional;

enum LoginClient {
    PAGE("page", false),
    ADMIN("admin", true);

    private final String registrationId;
    private final boolean grantsAdministrator;

    LoginClient(String registrationId, boolean grantsAdministrator) {
        this.registrationId = registrationId;
        this.grantsAdministrator = grantsAdministrator;
    }

    boolean grantsAdministrator() {
        return grantsAdministrator;
    }

    static Optional<LoginClient> find(String registrationId) {
        return Arrays.stream(values()).filter(client -> client.registrationId.equals(registrationId)).findFirst();
    }

    static LoginClient of(String registrationId) {
        return find(registrationId)
            .orElseThrow(() -> new IllegalStateException("Unknown OAuth2 registration: " + registrationId));
    }
}
