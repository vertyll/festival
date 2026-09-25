package com.vertyll.festival.common;

import org.bson.types.ObjectId;

public record Reference(ObjectId id, LocalizedText name) {
}
