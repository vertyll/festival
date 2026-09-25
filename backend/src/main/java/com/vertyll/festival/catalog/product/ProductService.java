package com.vertyll.festival.catalog.product;

import java.time.Clock;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.bson.types.ObjectId;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.vertyll.festival.catalog.category.CategoryDeletedEvent;
import com.vertyll.festival.catalog.category.CategoryService;
import com.vertyll.festival.common.InvalidRequestException;
import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.NotFoundException;
import com.vertyll.festival.common.Reference;
import com.vertyll.festival.common.SearchPatterns;
import com.vertyll.festival.settings.ShopSettingsService;

import lombok.RequiredArgsConstructor;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@RequiredArgsConstructor
class ProductService {

    private final ProductRepository repository;
    private final MongoTemplate mongo;
    private final CategoryService categories;
    private final ShopSettingsService settings;
    private final ProductMapper mapper;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    List<ProductCardResponse> searchCards(ProductSearch search) {
        return mongo.find(toQuery(search), ProductDocument.class).stream().map(mapper::toCard).toList();
    }

    List<ProductAdminResponse> findAllForAdmin() {
        return mongo.find(toQuery(ProductSearch.all()), ProductDocument.class)
            .stream()
            .map(mapper::toAdminResponse)
            .toList();
    }

    ProductAdminResponse findForAdmin(ObjectId id) {
        return mapper.toAdminResponse(get(id));
    }

    ProductDetailsResponse details(ObjectId id) {
        ProductDocument product = get(id);
        ObjectId categoryId = product.categoryId();
        List<Reference> categoryPath = categoryId == null ? List.of() : categories.pathTo(categoryId);
        return ProductDetailsResponse.of(product, settings.current(), categoryPath);
    }

    ProductAdminResponse create(ProductRequest request) {
        requireCategoryExists(request.categoryId());
        return mapper.toAdminResponse(repository.insert(ProductDocument.create(request, clock.instant())));
    }

    ProductAdminResponse update(ObjectId id, ProductRequest request) {
        ProductDocument current = get(id);
        requireCategoryExists(request.categoryId());
        return mapper.toAdminResponse(repository.save(current.update(request, clock.instant())));
    }

    void delete(ObjectId id) {
        repository.delete(get(id));
        events.publishEvent(new ProductDeletedEvent(id));
    }

    @EventListener
    void onCategoryDeleted(CategoryDeletedEvent event) {
        mongo.updateMulti(
            query(where("categoryId").is(event.categoryId())),
            new Update().unset("categoryId"),
            ProductDocument.class
        );
    }

    private static Query toQuery(ProductSearch search) {
        Query query = new Query().with(search.sort().toSort());
        String term = search.term();
        if (term != null) {
            Pattern pattern = SearchPatterns.containsIgnoreCase(term);
            query.addCriteria(
                new Criteria().orOperator(
                    Stream
                        .concat(
                            LocalizedText.fieldPaths("name").stream(),
                            LocalizedText.fieldPaths("description").stream()
                        )
                        .map(field -> where(field).regex(pattern))
                        .toList()
                )
            );
        }
        if (!search.ids().isEmpty()) {
            query.addCriteria(where("id").in(search.ids()));
        }
        Integer limit = search.limit();
        return limit == null ? query : query.limit(limit);
    }

    private void requireCategoryExists(@Nullable ObjectId categoryId) {
        if (categoryId != null && !categories.exists(categoryId)) {
            throw new InvalidRequestException(MessageKeys.PRODUCT_CATEGORY_NOT_FOUND);
        }
    }

    private ProductDocument get(ObjectId id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(MessageKeys.PRODUCT_NOT_FOUND));
    }
}
