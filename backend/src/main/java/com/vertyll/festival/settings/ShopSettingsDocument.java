package com.vertyll.festival.settings;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("settings")
record ShopSettingsDocument(
    @Id String id,
    BigDecimal shippingPrice,
    boolean stockVisible,
    boolean variantStockVisible,
    Instant updatedAt
) {

    static final String ID = "shop";

    ShopSettings toSettings() {
        return new ShopSettings(shippingPrice, stockVisible, variantStockVisible);
    }
}
