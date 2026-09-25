package com.vertyll.festival.security;

import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class ActiveAdministratorAuthorization implements AuthorizationManager<RequestAuthorizationContext> {

    private final AdministratorDirectory administrators;

    @Override
    public AuthorizationResult authorize(
        Supplier<? extends @Nullable Authentication> authentication,
        RequestAuthorizationContext context
    ) {
        Authentication current = authentication.get();
        boolean granted = current != null && UserIdentity.from(current).filter(this::isActiveAdministrator).isPresent();
        return new AuthorizationDecision(granted);
    }

    boolean isActiveAdministrator(UserIdentity identity) {
        return identity.administrator() && administrators.isAdministrator(identity.email());
    }
}
