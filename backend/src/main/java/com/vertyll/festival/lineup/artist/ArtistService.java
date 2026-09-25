package com.vertyll.festival.lineup.artist;

import java.time.Clock;
import java.util.List;

import org.bson.types.ObjectId;
import org.jspecify.annotations.Nullable;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.vertyll.festival.common.InvalidRequestException;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.MongoQueries;
import com.vertyll.festival.common.NotFoundException;
import com.vertyll.festival.common.Reference;
import com.vertyll.festival.lineup.stage.StageDeletedEvent;
import com.vertyll.festival.lineup.stage.StageService;

import lombok.RequiredArgsConstructor;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@RequiredArgsConstructor
class ArtistService {

    private final ArtistRepository repository;
    private final MongoTemplate mongo;
    private final StageService stages;
    private final ArtistMapper mapper;
    private final Clock clock;

    List<ArtistResponse> findNewest(@Nullable Integer limit) {
        return mongo.find(MongoQueries.newestFirst(limit), ArtistDocument.class)
            .stream()
            .map(mapper::toResponse)
            .toList();
    }

    ArtistResponse findById(ObjectId id) {
        return mapper.toResponse(get(id));
    }

    ArtistDetailsResponse details(ObjectId id) {
        ArtistDocument artist = get(id);
        ObjectId stageId = artist.stageId();
        Reference stage = stageId == null ? null : stages.findReference(stageId)
            .orElseThrow(() -> new IllegalStateException("Artist " + id + " points to a deleted stage"));
        return ArtistDetailsResponse.of(artist, stage);
    }

    ArtistResponse create(ArtistRequest request) {
        requireStageExists(request.stageId());
        return mapper.toResponse(repository.insert(ArtistDocument.create(request, clock.instant())));
    }

    ArtistResponse update(ObjectId id, ArtistRequest request) {
        ArtistDocument current = get(id);
        requireStageExists(request.stageId());
        return mapper.toResponse(repository.save(current.update(request, clock.instant())));
    }

    void delete(ObjectId id) {
        repository.delete(get(id));
    }

    @EventListener
    void onStageDeleted(StageDeletedEvent event) {
        mongo.updateMulti(
            query(where("stageId").is(event.stageId())),
            new Update().unset("stageId"),
            ArtistDocument.class
        );
    }

    private void requireStageExists(@Nullable ObjectId stageId) {
        if (stageId != null && !stages.exists(stageId)) {
            throw new InvalidRequestException(MessageKeys.ARTIST_STAGE_NOT_FOUND);
        }
    }

    private ArtistDocument get(ObjectId id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(MessageKeys.ARTIST_NOT_FOUND));
    }
}
