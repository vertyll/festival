package com.vertyll.festival.catalog.product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@RequiredArgsConstructor
public class ProductCatalog {

    private static final String VARIANTS = "variants";
    private static final String MATCHED_VARIANT_STOCK = "variants.$.stock";
    private static final long ONE_DOCUMENT = 1;

    private final ProductRepository repository;
    private final MongoTemplate mongo;
    private final ProductMapper mapper;

    public boolean exists(ObjectId id) {
        return repository.existsById(id);
    }

    public List<ProductCardResponse> cardsInOrder(List<ObjectId> ids) {
        Map<ObjectId, ProductDocument> byId = findAllById(ids);
        List<ProductCardResponse> cards = new ArrayList<>(ids.size());
        for (ObjectId id : ids) {
            ProductDocument product = byId.get(id);
            if (product != null) {
                cards.add(mapper.toCard(product));
            }
        }
        return List.copyOf(cards);
    }

    public Map<ObjectId, OrderableProduct> findOrderable(Collection<ObjectId> ids) {
        return findAllById(ids).values()
            .stream()
            .collect(Collectors.toUnmodifiableMap(ProductDocument::id, OrderableProduct::new));
    }

    public void reserve(OrderableProduct product, List<String> valueCodes, int quantity) {
        requirePositive(quantity);
        Query query = query(
            where("id").is(product.id())
                .and(VARIANTS)
                .elemMatch(where("valueCodes").is(valueCodes).and("stock").gte(quantity))
        );
        if (failsToUpdateOneVariant(query, -quantity)) {
            throw new InsufficientStockException(product.name());
        }
    }

    public void release(ObjectId productId, List<String> valueCodes, int quantity) {
        requirePositive(quantity);
        Query query = query(where("id").is(productId).and(VARIANTS).elemMatch(where("valueCodes").is(valueCodes)));
        if (failsToUpdateOneVariant(query, quantity)) {
            throw new IllegalStateException(
                "Failed to return " + quantity + " units of variant " + valueCodes + " for product " + productId
            );
        }
    }

    private boolean failsToUpdateOneVariant(Query query, int stockChange) {
        return mongo.updateFirst(query, new Update().inc(MATCHED_VARIANT_STOCK, stockChange), ProductDocument.class)
            .getModifiedCount() != ONE_DOCUMENT;
    }

    private Map<ObjectId, ProductDocument> findAllById(Collection<ObjectId> ids) {
        return repository.findAllById(ids)
            .stream()
            .collect(Collectors.toUnmodifiableMap(ProductDocument::id, Function.identity()));
    }

    private static void requirePositive(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive: " + quantity);
        }
    }
}
