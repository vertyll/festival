package com.vertyll.festival.shop.order;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.bson.types.ObjectId;

import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.ValidationLimits;
import com.vertyll.festival.shop.ShippingDetails;

record PlaceOrderRequest(
    @NotNull(message = MessageKeys.REQUIRED) @Valid ShippingDetails shipping,
    @NotEmpty(message = MessageKeys.CART_EMPTY) @Size(
        max = ValidationLimits.MAX_CART_LINES,
        message = MessageKeys.CART_TOO_MANY_LINES
    ) List<@Valid @NotNull(message = MessageKeys.REQUIRED) Item> items
) {

    static final int MAX_QUANTITY = 99;

    PlaceOrderRequest {
        items = List.copyOf(items);
    }

    record Item(
        @NotNull(message = MessageKeys.REQUIRED) ObjectId productId,
        @Size(
            max = ValidationLimits.MAX_PRODUCT_OPTIONS,
            message = MessageKeys.TOO_MANY_PRODUCT_OPTIONS
        ) Map<@Pattern(regexp = ValidationLimits.CODE_PATTERN, message = MessageKeys.CODE_INVALID) String, @NotNull(
            message = MessageKeys.REQUIRED
        ) @Pattern(regexp = ValidationLimits.CODE_PATTERN, message = MessageKeys.CODE_INVALID) String> selectedOptions,
        @Min(value = 1, message = MessageKeys.QUANTITY_TOO_SMALL) @Max(
            value = MAX_QUANTITY,
            message = MessageKeys.QUANTITY_TOO_LARGE
        ) int quantity
    ) {

        Item {
            selectedOptions = Map.copyOf(selectedOptions);
        }
    }
}
