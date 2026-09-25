package com.vertyll.festival.settings;

import java.math.BigDecimal;

public record ShopSettings(BigDecimal shippingPrice, boolean stockVisible, boolean variantStockVisible) {
}
