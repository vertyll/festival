package com.vertyll.festival.content.sponsor;

import java.time.Instant;
import java.util.List;

import org.bson.types.ObjectId;

record SponsorResponse(
    ObjectId id,
    String name,
    String link,
    List<String> images,
    Instant createdAt,
    Instant updatedAt
) {
}
