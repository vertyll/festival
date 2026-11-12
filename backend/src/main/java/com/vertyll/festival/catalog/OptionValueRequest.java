package com.vertyll.festival.catalog;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import com.vertyll.festival.common.LocalizedLength;
import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.ValidationLimits;

public record OptionValueRequest(
    @NotNull(
        message = MessageKeys.REQUIRED
    ) @Pattern(regexp = ValidationLimits.CODE_PATTERN, message = MessageKeys.CODE_INVALID) String code,
    @NotNull(message = MessageKeys.REQUIRED) @LocalizedLength(
        max = ValidationLimits.ATTRIBUTE_VALUE_MAX_LENGTH
    ) LocalizedText label
) {
}
