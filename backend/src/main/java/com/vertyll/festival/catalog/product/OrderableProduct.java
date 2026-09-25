package com.vertyll.festival.catalog.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.vertyll.festival.common.LocalizedText;

public final class OrderableProduct {

    private final ProductDocument product;

    OrderableProduct(ProductDocument product) {
        this.product = product;
    }

    public ObjectId id() {
        return product.id();
    }

    public LocalizedText name() {
        return product.name();
    }

    public BigDecimal price() {
        return product.price();
    }

    public List<String> valueCodesFor(Map<String, String> selection) {
        return product.valueCodesFor(selection);
    }

    public List<SelectedOption> describe(List<String> valueCodes) {
        return product.describe(valueCodes);
    }
}
