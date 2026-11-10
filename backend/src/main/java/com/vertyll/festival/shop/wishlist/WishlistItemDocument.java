package com.vertyll.festival.shop.wishlist;

import java.time.Instant;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("wishlist_items")
@CompoundIndex(
    name = "uk_wishlist_items_customer_id_product_id",
    def = "{'customerId': 1, 'productId': 1}",
    unique = true
)
record WishlistItemDocument(
    @Id ObjectId id,
    String customerId,
    @Indexed(name = "idx_wishlist_items_product_id") ObjectId productId,
    Instant createdAt
) {
}
