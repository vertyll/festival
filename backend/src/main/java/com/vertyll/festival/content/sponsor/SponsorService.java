package com.vertyll.festival.content.sponsor;

import java.time.Clock;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.MongoQueries;
import com.vertyll.festival.common.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class SponsorService {

    private final SponsorRepository repository;
    private final MongoTemplate mongo;
    private final SponsorMapper mapper;
    private final Clock clock;

    List<SponsorResponse> findAll() {
        return mongo.find(MongoQueries.newestFirst(null), SponsorDocument.class)
            .stream()
            .map(mapper::toResponse)
            .toList();
    }

    SponsorResponse findById(ObjectId id) {
        return mapper.toResponse(get(id));
    }

    SponsorResponse create(SponsorRequest request) {
        return mapper.toResponse(repository.insert(SponsorDocument.create(request, clock.instant())));
    }

    SponsorResponse update(ObjectId id, SponsorRequest request) {
        return mapper.toResponse(repository.save(get(id).update(request, clock.instant())));
    }

    void delete(ObjectId id) {
        repository.delete(get(id));
    }

    private SponsorDocument get(ObjectId id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(MessageKeys.SPONSOR_NOT_FOUND));
    }
}
