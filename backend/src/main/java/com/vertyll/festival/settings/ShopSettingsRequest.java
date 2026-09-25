package com.vertyll.festival.settings;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.ValidationLimits;

record ShopSettingsRequest(
    @NotNull(
        message = MessageKeys.SHIPPING_PRICE_INVALID
    ) @DecimalMin(value = "0.00", message = MessageKeys.SHIPPING_PRICE_INVALID) @DecimalMax(
        value = ValidationLimits.MAX_SHIPPING_PRICE,
        message = MessageKeys.TOO_LARGE
    ) @Digits(integer = 12, fraction = 2, message = MessageKeys.SHIPPING_PRICE_INVALID) BigDecimal shippingPrice,
    boolean stockVisible,
    boolean variantStockVisible
) {
}
