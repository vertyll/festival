package com.vertyll.festival.shop.wishlist;

import java.time.Instant;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("wishlist_items")
@CompoundIndex(name = "customer_product", def = "{'customerId': 1, 'productId': 1}", unique = true)
record WishlistItemDocument(@Id ObjectId id, String customerId, @Indexed ObjectId productId, Instant createdAt) {
}
