package com.vertyll.festival.content.sponsor;

import java.time.Instant;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("sponsors")
record SponsorDocument(
    @Id ObjectId id,
    String name,
    String link,
    List<String> images,
    Instant createdAt,
    Instant updatedAt
) {

    SponsorDocument {
        images = List.copyOf(images);
    }

    static SponsorDocument create(SponsorRequest request, Instant now) {
        return new SponsorDocument(new ObjectId(), request.name(), request.link(), request.images(), now, now);
    }

    SponsorDocument update(SponsorRequest request, Instant now) {
        return new SponsorDocument(id, request.name(), request.link(), request.images(), createdAt, now);
    }
}
