package com.vertyll.festival.settings;

import java.math.BigDecimal;

record ShopSettingsResponse(
    BigDecimal shippingPrice,
    boolean stockVisible,
    boolean variantStockVisible,
    boolean checkoutEnabled,
    String currency
) {

    static ShopSettingsResponse of(ShopSettings settings, ShopProperties properties) {
        return new ShopSettingsResponse(
            settings.shippingPrice(),
            settings.stockVisible(),
            settings.variantStockVisible(),
            properties.checkoutEnabled(),
            properties.currency()
        );
    }
}
