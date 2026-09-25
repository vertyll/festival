package com.vertyll.festival.catalog.category;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.jspecify.annotations.Nullable;

import com.vertyll.festival.common.InvalidRequestException;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.Reference;

final class CategoryTree {

    private final Map<ObjectId, CategoryDocument> byId;

    private CategoryTree(Map<ObjectId, CategoryDocument> byId) {
        this.byId = byId;
    }

    static CategoryTree of(List<CategoryDocument> categories) {
        return new CategoryTree(
            categories.stream().collect(Collectors.toUnmodifiableMap(CategoryDocument::id, Function.identity()))
        );
    }

    List<Reference> pathTo(ObjectId categoryId) {
        Deque<Reference> path = new ArrayDeque<>();
        Set<ObjectId> visited = new HashSet<>();
        ObjectId current = categoryId;
        while (current != null) {
            if (!visited.add(current)) {
                throw new IllegalStateException("Circular reference in a category tree: " + current);
            }
            CategoryDocument category = require(current);
            path.addFirst(new Reference(category.id(), category.name()));
            current = category.parentId();
        }
        return List.copyOf(path);
    }

    @Nullable Reference parentOf(CategoryDocument category) {
        ObjectId parentId = category.parentId();
        if (parentId == null) {
            return null;
        }
        CategoryDocument parent = require(parentId);
        return new Reference(parent.id(), parent.name());
    }

    void validateParent(ObjectId id, @Nullable ObjectId parentId) {
        if (parentId == null) {
            return;
        }
        if (!byId.containsKey(parentId)) {
            throw new InvalidRequestException(MessageKeys.CATEGORY_PARENT_NOT_FOUND);
        }
        if (pathTo(parentId).stream().anyMatch(ancestor -> ancestor.id().equals(id))) {
            throw new InvalidRequestException(MessageKeys.CATEGORY_CYCLE);
        }
    }

    private CategoryDocument require(ObjectId id) {
        CategoryDocument category = byId.get(id);
        if (category == null) {
            throw new IllegalStateException("Category " + id + " points to a non-existent parent category");
        }
        return category;
    }
}
