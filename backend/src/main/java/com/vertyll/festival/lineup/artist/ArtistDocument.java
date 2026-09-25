package com.vertyll.festival.lineup.artist;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.bson.types.ObjectId;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.vertyll.festival.common.LocalizedText;

@Document("artists")
record ArtistDocument(
    @Id ObjectId id,
    String name,
    @Nullable LocalizedText description,
    List<String> images,
    @Indexed @Nullable ObjectId stageId,
    @Nullable LocalDate concertDate,
    @Nullable LocalTime concertTime,
    Instant createdAt,
    Instant updatedAt
) {

    ArtistDocument {
        images = List.copyOf(images);
    }

    static ArtistDocument create(ArtistRequest request, Instant now) {
        return new ArtistDocument(
            new ObjectId(),
            request.name(),
            request.description(),
            request.images(),
            request.stageId(),
            request.concertDate(),
            request.concertTime(),
            now,
            now
        );
    }

    ArtistDocument update(ArtistRequest request, Instant now) {
        return new ArtistDocument(
            id,
            request.name(),
            request.description(),
            request.images(),
            request.stageId(),
            request.concertDate(),
            request.concertTime(),
            createdAt,
            now
        );
    }
}
