package com.vertyll.festival.catalog.product;

import java.math.BigDecimal;
import java.util.List;

import org.bson.types.ObjectId;
import org.jspecify.annotations.Nullable;

import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.Reference;
import com.vertyll.festival.settings.ShopSettings;

record ProductDetailsResponse(
    ObjectId id,
    LocalizedText name,
    @Nullable LocalizedText description,
    BigDecimal price,
    List<String> images,
    List<ProductOption> options,
    List<VariantAvailability> variants,
    @Nullable Integer totalStock,
    List<Reference> categoryPath
) {

    record VariantAvailability(List<String> valueCodes, boolean available, @Nullable Integer stock) {
    }

    static ProductDetailsResponse of(ProductDocument product, ShopSettings settings, List<Reference> categoryPath) {
        List<VariantAvailability> variants = product.variants()
            .stream()
            .map(
                variant -> new VariantAvailability(
                    variant.valueCodes(),
                    variant.stock() > 0,
                    settings.variantStockVisible() ? variant.stock() : null
                )
            )
            .toList();
        return new ProductDetailsResponse(
            product.id(),
            product.name(),
            product.description(),
            product.price(),
            product.images(),
            product.options(),
            variants,
            settings.stockVisible() ? product.totalStock() : null,
            categoryPath
        );
    }
}
