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
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class FestivalOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OidcUserService delegate = new OidcUserService();
    private final JwtDecoder accessTokens;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser user = Objects.requireNonNull(delegate.loadUser(userRequest));
        if (user.getEmail() == null || !Boolean.TRUE.equals(user.getEmailVerified())) {
            throw denied("The account must have a verified e-mail address.");
        }
        boolean administrator =
                Roles.isAdministrator(accessTokens.decode(userRequest.getAccessToken().getTokenValue()));
        LoginClient client = LoginClient.of(userRequest.getClientRegistration().getRegistrationId());
        if (client.requiresAdministrator() && !administrator) {
            throw denied("This account has no access to the admin panel.");
        }
        List<GrantedAuthority> authorities = administrator ? List
            .of(new SimpleGrantedAuthority(Roles.USER_AUTHORITY), new SimpleGrantedAuthority(Roles.ADMIN_AUTHORITY))
                : List.of(new SimpleGrantedAuthority(Roles.USER_AUTHORITY));
        return new DefaultOidcUser(authorities, user.getIdToken(), user.getUserInfo(), StandardClaimNames.SUB);
    }

    private static OAuth2AuthenticationException denied(String description) {
        return new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.ACCESS_DENIED, description, null));
    }
}
