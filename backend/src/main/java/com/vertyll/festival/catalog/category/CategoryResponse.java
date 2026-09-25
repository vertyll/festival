package com.vertyll.festival.catalog.category;

import org.bson.types.ObjectId;
import org.jspecify.annotations.Nullable;

import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.Reference;

record CategoryResponse(ObjectId id, LocalizedText name, @Nullable Reference parent) {
}
