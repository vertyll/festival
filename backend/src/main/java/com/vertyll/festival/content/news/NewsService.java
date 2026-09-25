package com.vertyll.festival.content.news;

import java.time.Clock;
import java.util.List;

import org.bson.types.ObjectId;
import org.jspecify.annotations.Nullable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.MongoQueries;
import com.vertyll.festival.common.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class NewsService {

    private final NewsRepository repository;
    private final MongoTemplate mongo;
    private final NewsMapper mapper;
    private final Clock clock;

    List<NewsResponse> findNewest(@Nullable Integer limit) {
        return mongo.find(MongoQueries.newestFirst(limit), NewsDocument.class)
            .stream()
            .map(mapper::toResponse)
            .toList();
    }

    NewsResponse findById(ObjectId id) {
        return mapper.toResponse(get(id));
    }

    NewsResponse create(NewsRequest request) {
        return mapper.toResponse(repository.insert(NewsDocument.create(request, clock.instant())));
    }

    NewsResponse update(ObjectId id, NewsRequest request) {
        return mapper.toResponse(repository.save(get(id).update(request, clock.instant())));
    }

    void delete(ObjectId id) {
        repository.delete(get(id));
    }

    private NewsDocument get(ObjectId id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(MessageKeys.NEWS_NOT_FOUND));
    }
}
