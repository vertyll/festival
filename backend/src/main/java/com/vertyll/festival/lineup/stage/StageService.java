package com.vertyll.festival.lineup.stage;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.NotFoundException;
import com.vertyll.festival.common.Reference;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StageService {

    private final StageRepository repository;
    private final StageMapper mapper;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    public boolean exists(ObjectId id) {
        return repository.existsById(id);
    }

    public Optional<Reference> findReference(ObjectId id) {
        return repository.findById(id).map(stage -> new Reference(stage.id(), stage.name()));
    }

    List<StageResponse> findAll() {
        return repository.findAll(Sort.by(LocalizedText.defaultLanguagePath("name")))
            .stream()
            .map(mapper::toResponse)
            .toList();
    }

    StageResponse create(StageRequest request) {
        return mapper.toResponse(repository.insert(StageDocument.create(request, clock.instant())));
    }

    StageResponse update(ObjectId id, StageRequest request) {
        return mapper.toResponse(repository.save(get(id).update(request, clock.instant())));
    }

    void delete(ObjectId id) {
        repository.delete(get(id));
        events.publishEvent(new StageDeletedEvent(id));
    }

    private StageDocument get(ObjectId id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(MessageKeys.STAGE_NOT_FOUND));
    }
}
