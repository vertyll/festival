package com.vertyll.festival.security;

import org.jspecify.annotations.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
class MeController {

    @GetMapping
    MeResponse me() {
        return UserIdentity.current().map(UserView::of).map(MeResponse::new).orElseGet(MeResponse::anonymous);
    }

    record MeResponse(@Nullable UserView user) {

        static MeResponse anonymous() {
            return new MeResponse(null);
        }
    }

    record UserView(String email, String name, @Nullable String picture, boolean administrator) {

        static UserView of(UserIdentity identity) {
            return new UserView(identity.email(), identity.name(), identity.picture(), identity.administrator());
        }
    }
}
