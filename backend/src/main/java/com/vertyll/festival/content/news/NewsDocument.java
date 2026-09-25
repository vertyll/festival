package com.vertyll.festival.content.news;

import java.time.Instant;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.vertyll.festival.common.LocalizedText;

@Document("news")
record NewsDocument(
    @Id ObjectId id,
    LocalizedText name,
    LocalizedText description,
    List<String> images,
    Instant createdAt,
    Instant updatedAt
) {

    NewsDocument {
        images = List.copyOf(images);
    }

    static NewsDocument create(NewsRequest request, Instant now) {
        return new NewsDocument(new ObjectId(), request.name(), request.description(), request.images(), now, now);
    }

    NewsDocument update(NewsRequest request, Instant now) {
        return new NewsDocument(id, request.name(), request.description(), request.images(), createdAt, now);
    }
}
