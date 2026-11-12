package com.vertyll.festival.catalog.attribute;

import java.time.Clock;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class AttributeService {

    private final AttributeRepository repository;
    private final AttributeMapper mapper;
    private final Clock clock;

    List<AttributeResponse> findAll() {
        return repository.findAll(Sort.by(LocalizedText.defaultLanguagePath("name")))
            .stream()
            .map(mapper::toResponse)
            .toList();
    }

    AttributeResponse create(AttributeRequest request) {
        AttributeDocument attribute =
                AttributeDocument.create(request.name(), mapper.toValues(request.values()), clock.instant());
        return mapper.toResponse(repository.insert(attribute));
    }

    AttributeResponse update(ObjectId id, AttributeRequest request) {
        AttributeDocument attribute =
                get(id).update(request.name(), mapper.toValues(request.values()), clock.instant());
        return mapper.toResponse(repository.save(attribute));
    }

    void delete(ObjectId id) {
        repository.delete(get(id));
    }

    private AttributeDocument get(ObjectId id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(MessageKeys.ATTRIBUTE_NOT_FOUND));
    }
}
