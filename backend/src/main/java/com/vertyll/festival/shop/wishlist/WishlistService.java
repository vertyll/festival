package com.vertyll.festival.shop.wishlist;

import java.time.Clock;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.vertyll.festival.catalog.product.ProductCardResponse;
import com.vertyll.festival.catalog.product.ProductCatalog;
import com.vertyll.festival.catalog.product.ProductDeletedEvent;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.NotFoundException;

import lombok.RequiredArgsConstructor;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@RequiredArgsConstructor
class WishlistService {

    private final MongoTemplate mongo;
    private final ProductCatalog catalog;
    private final Clock clock;

    List<ProductCardResponse> products(String customerId) {
        return catalog.cardsInOrder(productIds(customerId));
    }

    List<ObjectId> productIds(String customerId) {
        Query query = query(where("customerId").is(customerId)).with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongo.find(query, WishlistItemDocument.class).stream().map(WishlistItemDocument::productId).toList();
    }

    void add(String customerId, ObjectId productId) {
        if (!catalog.exists(productId)) {
            throw new NotFoundException(MessageKeys.PRODUCT_NOT_FOUND);
        }
        mongo.upsert(
            entry(customerId, productId),
            new Update().setOnInsert("createdAt", clock.instant()),
            WishlistItemDocument.class
        );
    }

    void remove(String customerId, ObjectId productId) {
        mongo.remove(entry(customerId, productId), WishlistItemDocument.class);
    }

    @EventListener
    void onProductDeleted(ProductDeletedEvent event) {
        mongo.remove(query(where("productId").is(event.productId())), WishlistItemDocument.class);
    }

    private static Query entry(String customerId, ObjectId productId) {
        return query(where("customerId").is(customerId).and("productId").is(productId));
    }
}
