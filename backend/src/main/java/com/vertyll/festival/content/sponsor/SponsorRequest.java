package com.vertyll.festival.content.sponsor;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.vertyll.festival.common.HttpUrl;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.ValidationLimits;
import com.vertyll.festival.media.MediaUrl;

record SponsorRequest(
    @NotBlank(
        message = MessageKeys.REQUIRED
    ) @Size(max = ValidationLimits.NAME_MAX_LENGTH, message = MessageKeys.TOO_LONG) String name,
    @NotBlank(message = MessageKeys.REQUIRED) @HttpUrl String link,
    @Size(max = ValidationLimits.MAX_IMAGES, message = MessageKeys.TOO_MANY_IMAGES) List<@MediaUrl String> images
) {

    SponsorRequest {
        name = name.strip();
        link = link.strip();
        images = List.copyOf(images);
    }
}
