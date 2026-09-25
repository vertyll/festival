package com.vertyll.festival.catalog.product;

import java.math.BigDecimal;
import java.util.List;

import org.bson.types.ObjectId;

import com.vertyll.festival.common.LocalizedText;

public record ProductCardResponse(ObjectId id, LocalizedText name, BigDecimal price, List<String> images) {
}
