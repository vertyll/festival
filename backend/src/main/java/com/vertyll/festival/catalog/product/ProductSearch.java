package com.vertyll.festival.catalog.product;

import java.util.List;

import org.bson.types.ObjectId;
import org.jspecify.annotations.Nullable;

record ProductSearch(@Nullable String term, List<ObjectId> ids, ProductSort sort, @Nullable Integer limit) {

    ProductSearch {
        ids = List.copyOf(ids);
    }

    static ProductSearch all() {
        return new ProductSearch(null, List.of(), ProductSort.NEWEST, null);
    }
}
