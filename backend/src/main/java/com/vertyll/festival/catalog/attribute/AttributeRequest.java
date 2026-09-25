package com.vertyll.festival.catalog.attribute;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.vertyll.festival.catalog.OptionValue;
import com.vertyll.festival.common.LocalizedLength;
import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.ValidationLimits;

record AttributeRequest(
    @NotNull(message = MessageKeys.REQUIRED) @LocalizedLength(
        max = ValidationLimits.NAME_MAX_LENGTH
    ) LocalizedText name,
    @NotEmpty(message = MessageKeys.OPTION_VALUES_REQUIRED) @Size(
        max = ValidationLimits.MAX_ATTRIBUTE_VALUES,
        message = MessageKeys.TOO_MANY_ATTRIBUTE_VALUES
    ) List<@Valid @NotNull(message = MessageKeys.REQUIRED) OptionValue> values
) {

    AttributeRequest {
        values = List.copyOf(values);
    }
}
