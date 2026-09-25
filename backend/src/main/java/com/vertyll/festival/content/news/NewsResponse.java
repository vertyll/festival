package com.vertyll.festival.content.news;

import java.time.Instant;
import java.util.List;

import org.bson.types.ObjectId;

import com.vertyll.festival.common.LocalizedText;

record NewsResponse(
    ObjectId id,
    LocalizedText name,
    LocalizedText description,
    List<String> images,
    Instant createdAt,
    Instant updatedAt
) {
}
