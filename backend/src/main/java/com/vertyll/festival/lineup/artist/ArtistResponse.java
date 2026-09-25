package com.vertyll.festival.lineup.artist;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.bson.types.ObjectId;
import org.jspecify.annotations.Nullable;

import com.vertyll.festival.common.LocalizedText;

import com.fasterxml.jackson.annotation.JsonFormat;

record ArtistResponse(
    ObjectId id,
    String name,
    @Nullable LocalizedText description,
    List<String> images,
    @Nullable ObjectId stageId,
    @Nullable LocalDate concertDate,
    @Nullable @JsonFormat(pattern = "HH:mm") LocalTime concertTime,
    Instant createdAt,
    Instant updatedAt
) {
}
