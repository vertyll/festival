package com.vertyll.festival.catalog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import com.vertyll.festival.common.InvalidRequestException;
import com.vertyll.festival.common.LocalizedLength;
import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.ValidationLimits;

public record OptionValue(
    @NotNull(
        message = MessageKeys.REQUIRED
    ) @Pattern(regexp = ValidationLimits.CODE_PATTERN, message = MessageKeys.CODE_INVALID) String code,
    @NotNull(message = MessageKeys.REQUIRED) @LocalizedLength(
        max = ValidationLimits.ATTRIBUTE_VALUE_MAX_LENGTH
    ) LocalizedText label
) {

    public static void requireUniqueCodes(List<OptionValue> values) {
        Set<String> codes = new HashSet<>();
        for (OptionValue value : values) {
            if (!codes.add(value.code())) {
                throw new InvalidRequestException(MessageKeys.OPTION_VALUE_DUPLICATED);
            }
        }
    }
}
