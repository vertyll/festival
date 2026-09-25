package com.vertyll.festival.catalog.attribute;

import java.time.Instant;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.vertyll.festival.catalog.OptionValue;
import com.vertyll.festival.common.LocalizedText;

@Document("attributes")
record AttributeDocument(
    @Id ObjectId id,
    LocalizedText name,
    List<OptionValue> values,
    Instant createdAt,
    Instant updatedAt
) {

    AttributeDocument {
        values = List.copyOf(values);
    }

    static AttributeDocument create(AttributeRequest request, Instant now) {
        OptionValue.requireUniqueCodes(request.values());
        return new AttributeDocument(new ObjectId(), request.name(), request.values(), now, now);
    }

    AttributeDocument update(AttributeRequest request, Instant now) {
        OptionValue.requireUniqueCodes(request.values());
        return new AttributeDocument(id, request.name(), request.values(), createdAt, now);
    }
}
