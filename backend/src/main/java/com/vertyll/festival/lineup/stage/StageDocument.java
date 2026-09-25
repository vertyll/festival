package com.vertyll.festival.lineup.stage;

import java.time.Instant;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.vertyll.festival.common.LocalizedText;

@Document("stages")
record StageDocument(@Id ObjectId id, LocalizedText name, Instant createdAt, Instant updatedAt) {

    static StageDocument create(StageRequest request, Instant now) {
        return new StageDocument(new ObjectId(), request.name(), now, now);
    }

    StageDocument update(StageRequest request, Instant now) {
        return new StageDocument(id, request.name(), createdAt, now);
    }
}
