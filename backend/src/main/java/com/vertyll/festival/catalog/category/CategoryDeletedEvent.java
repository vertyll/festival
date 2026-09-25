package com.vertyll.festival.catalog.category;

import org.bson.types.ObjectId;

public record CategoryDeletedEvent(ObjectId categoryId) {
}
