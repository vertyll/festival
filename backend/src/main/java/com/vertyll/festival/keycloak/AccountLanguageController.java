package com.vertyll.festival.keycloak;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vertyll.festival.common.InvalidRequestException;
import com.vertyll.festival.common.Language;
import com.vertyll.festival.common.MessageKeys;

import lombok.RequiredArgsConstructor;

/**
 * Stores the language chosen in the application on the caller's Keycloak account, which Keycloak's pages show a
 * signed-in person in, so they open in the application's language however the person reaches them.
 */
@RestController
@RequestMapping("/api/account/language")
@RequiredArgsConstructor
class AccountLanguageController {

    private final KeycloakAccount account;

    @PutMapping
    LanguageResponse keepLanguage(Authentication authentication, @Valid @RequestBody LanguageRequest request) {
        if (!(authentication.getCredentials() instanceof Jwt accessToken)) {
            throw new IllegalStateException("An API request is authorized by an access token");
        }
        Language language = Language.fromCode(request.language())
            .orElseThrow(() -> new InvalidRequestException(MessageKeys.INVALID_VALUE));
        account.keepLanguage(accessToken, language);
        return new LanguageResponse(language.code());
    }

    record LanguageRequest(@NotBlank(message = MessageKeys.REQUIRED) String language) {
    }

    record LanguageResponse(String language) {
    }
}
