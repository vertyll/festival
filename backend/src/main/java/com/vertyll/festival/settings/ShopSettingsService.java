package com.vertyll.festival.settings;

import java.time.Clock;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@RequiredArgsConstructor
public class ShopSettingsService implements SmartInitializingSingleton {

    private final ShopSettingsRepository repository;
    private final MongoTemplate mongo;
    private final ShopProperties properties;
    private final Clock clock;

    public ShopSettings current() {
        return repository.findById(ShopSettingsDocument.ID)
            .map(ShopSettingsDocument::toSettings)
            .orElseThrow(
                () -> new IllegalStateException("Shop settings document is missing from the settings collection")
            );
    }

    ShopSettingsResponse currentResponse() {
        return ShopSettingsResponse.of(current(), properties);
    }

    ShopSettingsResponse update(ShopSettingsRequest request) {
        repository.save(
            new ShopSettingsDocument(
                ShopSettingsDocument.ID,
                request.shippingPrice(),
                request.stockVisible(),
                request.variantStockVisible(),
                clock.instant()
            )
        );
        return currentResponse();
    }

    @Override
    public void afterSingletonsInstantiated() {
        ShopProperties.InitialSettings initial = properties.initialSettings();
        mongo.upsert(
            query(where("id").is(ShopSettingsDocument.ID)),
            new Update().setOnInsert("shippingPrice", initial.shippingPrice())
                .setOnInsert("stockVisible", initial.stockVisible())
                .setOnInsert("variantStockVisible", initial.variantStockVisible())
                .setOnInsert("updatedAt", clock.instant()),
            ShopSettingsDocument.class
        );
    }
}
