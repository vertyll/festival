package com.vertyll.festival.security;

import java.util.Objects;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public record UserIdentity(
    String id,
    String email,
    @Nullable String name,
    @Nullable String picture,
    boolean administrator
) {

    public static Optional<UserIdentity> current() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException(
                "Missing security context - the request bypassed the Spring Security filters"
            );
        }
        return from(authentication);
    }

    static Optional<UserIdentity> from(Authentication authentication) {
        if (authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        if (!(authentication.getPrincipal() instanceof OidcUser user)) {
            throw new IllegalStateException("Unsupported authentication type: " + authentication.getClass().getName());
        }
        boolean administrator = authentication.getAuthorities()
            .stream()
            .anyMatch(authority -> Roles.ADMIN_AUTHORITY.equals(authority.getAuthority()));
        return Optional.of(
            new UserIdentity(
                Objects.requireNonNull(user.getSubject()),
                Objects.requireNonNull(user.getEmail()),
                user.getFullName(),
                user.getPicture(),
                administrator
            )
        );
    }
}
