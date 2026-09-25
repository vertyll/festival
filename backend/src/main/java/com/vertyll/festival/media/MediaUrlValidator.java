package com.vertyll.festival.media;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.jspecify.annotations.Nullable;

import com.vertyll.festival.common.ValidationLimits;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class MediaUrlValidator implements ConstraintValidator<MediaUrl, String> {

    private final MediaProperties properties;

    @Override
    public boolean isValid(@Nullable String value, ConstraintValidatorContext context) {
        return value == null || (value.length() <= ValidationLimits.URL_MAX_LENGTH && properties.isPublicUrl(value));
    }
}
