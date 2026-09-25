package com.vertyll.festival.lineup.stage;

import jakarta.validation.constraints.NotNull;

import com.vertyll.festival.common.LocalizedLength;
import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.ValidationLimits;

record StageRequest(
    @NotNull(message = MessageKeys.REQUIRED) @LocalizedLength(max = ValidationLimits.NAME_MAX_LENGTH) LocalizedText name
) {
}
