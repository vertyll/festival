package com.vertyll.festival.lineup.artist;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.bson.types.ObjectId;
import org.jspecify.annotations.Nullable;

import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.Reference;

import com.fasterxml.jackson.annotation.JsonFormat;

record ArtistDetailsResponse(
    ObjectId id,
    String name,
    @Nullable LocalizedText description,
    List<String> images,
    @Nullable Reference stage,
    @Nullable LocalDate concertDate,
    @Nullable @JsonFormat(pattern = "HH:mm") LocalTime concertTime
) {

    static ArtistDetailsResponse of(ArtistDocument artist, @Nullable Reference stage) {
        return new ArtistDetailsResponse(
            artist.id(),
            artist.name(),
            artist.description(),
            artist.images(),
            stage,
            artist.concertDate(),
            artist.concertTime()
        );
    }
}
