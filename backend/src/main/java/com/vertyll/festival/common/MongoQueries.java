package com.vertyll.festival.common;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;

public final class MongoQueries {

    private MongoQueries() {
    }

    public static Query newestFirst(@Nullable Integer limit) {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return limit == null ? query : query.limit(limit);
    }
}
