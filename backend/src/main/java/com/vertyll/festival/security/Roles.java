package com.vertyll.festival.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.oauth2.jwt.Jwt;

final class Roles {

    static final String USER = "USER";
    static final String ADMIN = "ADMIN";

    static final String USER_AUTHORITY = "ROLE_" + USER;
    static final String ADMIN_AUTHORITY = "ROLE_" + ADMIN;

    static final String KEYCLOAK_ADMIN = "ADMIN";

    private Roles() {
    }

    static boolean isAdministrator(Jwt accessToken) {
        Map<String, Object> realmAccess = accessToken.getClaimAsMap("realm_access");
        if (realmAccess == null || !(realmAccess.get("roles") instanceof Collection<?> roles)) {
            return false;
        }
        return List.copyOf(roles).contains(KEYCLOAK_ADMIN);
    }
}
