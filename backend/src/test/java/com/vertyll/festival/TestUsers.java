package com.vertyll.festival;

import java.util.Arrays;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.OidcLoginRequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;

public final class TestUsers {

    private TestUsers() {
    }

    public static OidcLoginRequestPostProcessor customer() {
        return user("google-customer", "klient@example.com", "ROLE_USER");
    }

    public static OidcLoginRequestPostProcessor admin() {
        return user("google-admin", "bootstrap-admin@example.com", "ROLE_USER", "ROLE_ADMIN");
    }

    public static OidcLoginRequestPostProcessor removedAdmin() {
        return user("google-removed-admin", "removed-admin@example.com", "ROLE_USER", "ROLE_ADMIN");
    }

    private static OidcLoginRequestPostProcessor user(String subject, String email, String... roles) {
        return oidcLogin().idToken(token -> token.subject(subject).claim("email", email).claim("email_verified", true))
            .authorities(Arrays.stream(roles).map(SimpleGrantedAuthority::new).toArray(GrantedAuthority[]::new));
    }
}
