package com.vertyll.festival.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Set;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.jspecify.annotations.Nullable;

public final class HttpUrlValidator implements ConstraintValidator<HttpUrl, String> {

    private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https");

    @Override
    public boolean isValid(@Nullable String value, ConstraintValidatorContext context) {
        return value == null || isHttpUrl(value);
    }

    static boolean isHttpUrl(String value) {
        if (value.isBlank() || value.length() > ValidationLimits.URL_MAX_LENGTH) {
            return false;
        }
        try {
            URI uri = new URI(value);
            String scheme = uri.getScheme();
            return scheme != null && uri.getHost() != null && ALLOWED_SCHEMES.contains(scheme.toLowerCase(Locale.ROOT));
        } catch (URISyntaxException _) {
            return false;
        }
    }
}
