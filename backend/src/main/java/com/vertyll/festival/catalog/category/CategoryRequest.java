package com.vertyll.festival.catalog.category;

import jakarta.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.jspecify.annotations.Nullable;

import com.vertyll.festival.common.LocalizedLength;
import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.ValidationLimits;

record CategoryRequest(
    @NotNull(message = MessageKeys.REQUIRED) @LocalizedLength(
        max = ValidationLimits.NAME_MAX_LENGTH
    ) LocalizedText name,
    @Nullable ObjectId parentId
) {
}
