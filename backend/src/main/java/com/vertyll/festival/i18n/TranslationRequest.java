package com.vertyll.festival.i18n;

import jakarta.validation.constraints.NotNull;

import com.vertyll.festival.common.LocalizedLength;
import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.ValidationLimits;

record TranslationRequest(
    @NotNull(message = MessageKeys.REQUIRED) @LocalizedLength(
        max = ValidationLimits.DESCRIPTION_MAX_LENGTH
    ) @IcuMessage LocalizedText messages
) {
}
