package com.vertyll.festival.catalog.product;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.bson.types.ObjectId;
import org.jspecify.annotations.Nullable;

import com.vertyll.festival.common.LocalizedLength;
import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.ValidationLimits;
import com.vertyll.festival.media.MediaUrl;

record ProductRequest(
    @NotNull(message = MessageKeys.REQUIRED) @LocalizedLength(
        max = ValidationLimits.NAME_MAX_LENGTH
    ) LocalizedText name,
    @Nullable @LocalizedLength(max = ValidationLimits.DESCRIPTION_MAX_LENGTH) LocalizedText description,
    @NotNull(
        message = MessageKeys.PRICE_INVALID
    ) @DecimalMin(value = "0.01", message = MessageKeys.PRICE_INVALID) @DecimalMax(
        value = ValidationLimits.MAX_PRICE,
        message = MessageKeys.TOO_LARGE
    ) @Digits(integer = 12, fraction = 2, message = MessageKeys.PRICE_INVALID) BigDecimal price,
    @Nullable ObjectId categoryId,
    @Size(max = ValidationLimits.MAX_IMAGES, message = MessageKeys.TOO_MANY_IMAGES) List<@MediaUrl String> images,
    @Size(
        max = ValidationLimits.MAX_PRODUCT_OPTIONS,
        message = MessageKeys.TOO_MANY_PRODUCT_OPTIONS
    ) List<@Valid @NotNull(message = MessageKeys.REQUIRED) ProductOption> options,
    @NotEmpty(message = MessageKeys.VARIANTS_REQUIRED) @Size(
        max = ProductVariants.MAX_VARIANTS,
        message = MessageKeys.PRODUCT_TOO_MANY_VARIANTS
    ) List<@Valid @NotNull(message = MessageKeys.REQUIRED) ProductVariant> variants
) {

    ProductRequest {
        images = List.copyOf(images);
        options = List.copyOf(options);
        variants = List.copyOf(variants);
    }
}
