package com.vertyll.festival.security;

import org.jspecify.annotations.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
class MeController {

    private final ActiveAdministratorAuthorization activeAdministrator;

    @GetMapping
    MeResponse me() {
        return UserIdentity.current()
            .map(identity -> UserView.of(identity, activeAdministrator.isActiveAdministrator(identity)))
            .map(MeResponse::new)
            .orElseGet(MeResponse::anonymous);
    }

    record MeResponse(@Nullable UserView user) {

        static MeResponse anonymous() {
            return new MeResponse(null);
        }
    }

    record UserView(String email, @Nullable String name, @Nullable String picture, boolean administrator) {

        static UserView of(UserIdentity identity, boolean administrator) {
            return new UserView(identity.email(), identity.name(), identity.picture(), administrator);
        }
    }
}
