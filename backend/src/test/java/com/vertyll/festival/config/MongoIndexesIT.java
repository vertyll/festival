package com.vertyll.festival.config;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.vertyll.festival.IntegrationTest;

import com.mongodb.client.model.IndexOptions;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class MongoIndexesIT {

    private static final String WISHLIST = "wishlist_items";
    private static final String ORDERS = "orders";

    @Autowired
    MongoTemplate mongo;

    @Test
    void indexesAnEarlierReleaseNamedAreReplacedByTheDeclaredOnes() {
        mongo.getCollection(WISHLIST).dropIndexes();
        mongo.getCollection(ORDERS).dropIndexes();
        mongo.getCollection(WISHLIST)
            .createIndex(
                new Document("customerId", 1).append("productId", 1),
                new IndexOptions().name("customer_product").unique(true)
            );
        mongo.getCollection(WISHLIST).createIndex(new Document("productId", 1), new IndexOptions().name("productId"));
        mongo.getCollection(ORDERS)
            .createIndex(
                new Document("customerId", 1).append("createdAt", -1),
                new IndexOptions().name("customer_newest")
            );

        new MongoIndexes(mongo).afterSingletonsInstantiated();
        new MongoIndexes(mongo).afterSingletonsInstantiated();

        assertThat(indexes(WISHLIST)).extracting(index -> index.getString("name"))
            .containsExactlyInAnyOrder(
                "_id_",
                "uk_wishlist_items_customer_id_product_id",
                "idx_wishlist_items_product_id"
            );
        assertThat(indexes(WISHLIST)).filteredOn(index -> index.getString("name").startsWith("uk_"))
            .singleElement()
            .satisfies(index -> assertThat(index.getBoolean("unique")).isTrue());
        assertThat(indexes(ORDERS)).extracting(index -> index.getString("name"))
            .containsExactlyInAnyOrder("_id_", "idx_orders_customer_id_created_at");
    }

    @Test
    void everyDeclaredIndexIsCreatedAtStartUp() {
        assertThat(indexes("categories")).extracting(index -> index.getString("name"))
            .contains("idx_categories_parent_id");
        assertThat(indexes("products")).extracting(index -> index.getString("name"))
            .contains("idx_products_category_id");
        assertThat(indexes("artists")).extracting(index -> index.getString("name")).contains("idx_artists_stage_id");
    }

    private List<Document> indexes(String collection) {
        List<Document> indexes = new ArrayList<>();
        mongo.getCollection(collection).listIndexes().forEach(indexes::add);
        return indexes;
    }
}
