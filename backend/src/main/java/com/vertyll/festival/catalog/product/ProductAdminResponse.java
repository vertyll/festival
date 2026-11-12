package com.vertyll.festival.catalog.product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.bson.types.ObjectId;
import org.jspecify.annotations.Nullable;

import com.vertyll.festival.common.LocalizedText;

record ProductAdminResponse(
    ObjectId id,
    LocalizedText name,
    @Nullable LocalizedText description,
    BigDecimal price,
    @Nullable ObjectId categoryId,
    List<String> images,
    List<ProductOptionResponse> options,
    List<Variant> variants,
    Instant createdAt,
    Instant updatedAt
) {

    record Variant(List<String> valueCodes, int stock) {
    }
}
