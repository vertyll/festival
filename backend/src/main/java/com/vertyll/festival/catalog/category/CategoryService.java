package com.vertyll.festival.catalog.category;

import java.time.Clock;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.NotFoundException;
import com.vertyll.festival.common.Reference;

import lombok.RequiredArgsConstructor;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
    private final MongoTemplate mongo;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    public boolean exists(ObjectId id) {
        return repository.existsById(id);
    }

    public List<Reference> pathTo(ObjectId categoryId) {
        return tree().pathTo(categoryId);
    }

    List<CategoryResponse> findAll() {
        CategoryTree tree = tree();
        return repository.findAll(Sort.by(LocalizedText.defaultLanguagePath("name")))
            .stream()
            .map(category -> toResponse(category, tree))
            .toList();
    }

    CategoryResponse create(CategoryRequest request) {
        ObjectId id = new ObjectId();
        tree().validateParent(id, request.parentId());
        CategoryDocument saved = repository.insert(CategoryDocument.create(id, request, clock.instant()));
        return toResponse(saved, tree());
    }

    CategoryResponse update(ObjectId id, CategoryRequest request) {
        CategoryDocument current = get(id);
        tree().validateParent(id, request.parentId());
        CategoryDocument saved = repository.save(current.update(request, clock.instant()));
        return toResponse(saved, tree());
    }

    void delete(ObjectId id) {
        CategoryDocument current = get(id);
        mongo.updateMulti(query(where("parentId").is(id)), new Update().unset("parentId"), CategoryDocument.class);
        repository.delete(current);
        events.publishEvent(new CategoryDeletedEvent(id));
    }

    private CategoryTree tree() {
        return CategoryTree.of(repository.findAll());
    }

    private CategoryDocument get(ObjectId id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(MessageKeys.CATEGORY_NOT_FOUND));
    }

    private static CategoryResponse toResponse(CategoryDocument category, CategoryTree tree) {
        return new CategoryResponse(category.id(), category.name(), tree.parentOf(category));
    }
}
