package com.vertyll.festival.lineup.artist;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.bson.types.ObjectId;
import org.jspecify.annotations.Nullable;

import com.vertyll.festival.common.LocalizedLength;
import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.ValidationLimits;
import com.vertyll.festival.media.MediaUrl;

import com.fasterxml.jackson.annotation.JsonFormat;

record ArtistRequest(
    @NotBlank(
        message = MessageKeys.REQUIRED
    ) @Size(max = ValidationLimits.NAME_MAX_LENGTH, message = MessageKeys.TOO_LONG) String name,
    @Nullable @LocalizedLength(max = ValidationLimits.DESCRIPTION_MAX_LENGTH) LocalizedText description,
    @Size(max = ValidationLimits.MAX_IMAGES, message = MessageKeys.TOO_MANY_IMAGES) List<@MediaUrl String> images,
    @Nullable ObjectId stageId,
    @Nullable LocalDate concertDate,
    @Nullable @JsonFormat(pattern = "HH:mm") LocalTime concertTime
) {

    ArtistRequest {
        name = name.strip();
        images = List.copyOf(images);
    }
}
