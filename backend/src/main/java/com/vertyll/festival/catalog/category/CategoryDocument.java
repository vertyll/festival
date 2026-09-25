package com.vertyll.festival.catalog.category;

import java.time.Instant;

import org.bson.types.ObjectId;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.vertyll.festival.common.LocalizedText;

@Document("categories")
record CategoryDocument(
    @Id ObjectId id,
    LocalizedText name,
    @Indexed @Nullable ObjectId parentId,
    Instant createdAt,
    Instant updatedAt
) {

    static CategoryDocument create(ObjectId id, CategoryRequest request, Instant now) {
        return new CategoryDocument(id, request.name(), request.parentId(), now, now);
    }

    CategoryDocument update(CategoryRequest request, Instant now) {
        return new CategoryDocument(id, request.name(), request.parentId(), createdAt, now);
    }
}
