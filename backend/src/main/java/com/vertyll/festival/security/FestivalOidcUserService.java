package com.vertyll.festival.security;

import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class FestivalOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OidcUserService delegate = new OidcUserService();
    private final AdministratorDirectory administrators;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser user = Objects.requireNonNull(delegate.loadUser(userRequest));
        String email = user.getEmail();
        if (email == null || !Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error(
                    OAuth2ErrorCodes.ACCESS_DENIED,
                    "The Google account must have a verified e-mail address.",
                    null
                )
            );
        }
        LoginClient client = LoginClient.of(userRequest.getClientRegistration().getRegistrationId());
        List<GrantedAuthority> authorities = client.grantsAdministrator() ? adminAuthorities(email)
                : List.of(new SimpleGrantedAuthority(Roles.USER_AUTHORITY));
        return new DefaultOidcUser(authorities, user.getIdToken(), user.getUserInfo(), StandardClaimNames.SUB);
    }

    private List<GrantedAuthority> adminAuthorities(String email) {
        if (!administrators.isAdministrator(email)) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error(OAuth2ErrorCodes.ACCESS_DENIED, "This account has no access to the admin panel.", null)
            );
        }
        return List
            .of(new SimpleGrantedAuthority(Roles.USER_AUTHORITY), new SimpleGrantedAuthority(Roles.ADMIN_AUTHORITY));
    }
}
