package com.vertyll.festival.content.news;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.vertyll.festival.common.LocalizedLength;
import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.ValidationLimits;
import com.vertyll.festival.media.MediaUrl;

record NewsRequest(
    @NotNull(message = MessageKeys.REQUIRED) @LocalizedLength(
        max = ValidationLimits.NAME_MAX_LENGTH
    ) LocalizedText name,
    @NotNull(message = MessageKeys.REQUIRED) @LocalizedLength(
        max = ValidationLimits.DESCRIPTION_MAX_LENGTH
    ) LocalizedText description,
    @Size(max = ValidationLimits.MAX_IMAGES, message = MessageKeys.TOO_MANY_IMAGES) List<@MediaUrl String> images
) {

    NewsRequest {
        images = List.copyOf(images);
    }
}
