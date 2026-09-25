package com.vertyll.festival.catalog.product;

import org.bson.types.ObjectId;

public record ProductDeletedEvent(ObjectId productId) {
}
