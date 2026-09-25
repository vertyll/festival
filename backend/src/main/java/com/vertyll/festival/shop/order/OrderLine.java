package com.vertyll.festival.shop.order;

import java.math.BigDecimal;
import java.util.List;

import org.bson.types.ObjectId;

import com.vertyll.festival.catalog.product.OrderableProduct;
import com.vertyll.festival.catalog.product.SelectedOption;
import com.vertyll.festival.common.LocalizedText;

record OrderLine(
    ObjectId productId,
    LocalizedText productName,
    List<String> valueCodes,
    List<SelectedOption> selection,
    BigDecimal unitPrice,
    int quantity,
    BigDecimal lineTotal
) {

    OrderLine {
        valueCodes = List.copyOf(valueCodes);
        selection = List.copyOf(selection);
    }

    static OrderLine of(OrderableProduct product, List<String> valueCodes, int quantity) {
        return new OrderLine(
            product.id(),
            product.name(),
            valueCodes,
            product.describe(valueCodes),
            product.price(),
            quantity,
            product.price().multiply(BigDecimal.valueOf(quantity))
        );
    }
}
