package com.vertyll.festival.catalog.product;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.ValidationLimits;

record ProductVariant(
    @Size(
        max = ValidationLimits.MAX_PRODUCT_OPTIONS,
        message = MessageKeys.TOO_MANY_PRODUCT_OPTIONS
    ) List<@NotNull(
        message = MessageKeys.REQUIRED
    ) @Pattern(regexp = ValidationLimits.CODE_PATTERN, message = MessageKeys.CODE_INVALID) String> valueCodes,
    @PositiveOrZero(
        message = MessageKeys.STOCK_INVALID
    ) @Max(value = ValidationLimits.MAX_STOCK, message = MessageKeys.TOO_LARGE) int stock
) {

    ProductVariant {
        valueCodes = List.copyOf(valueCodes);
    }
}
