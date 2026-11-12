package com.vertyll.festival.shop.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.bson.types.ObjectId;

import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.shop.ShippingDetailsResponse;

record OrderResponse(
    ObjectId id,
    OrderStatus status,
    List<Line> lines,
    ShippingDetailsResponse shipping,
    BigDecimal itemsTotal,
    BigDecimal shippingPrice,
    BigDecimal total,
    String currency,
    Instant createdAt
) {

    record Line(
        ObjectId productId,
        LocalizedText productName,
        List<String> valueCodes,
        List<SelectedOption> selection,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal
    ) {
    }

    record SelectedOption(LocalizedText option, LocalizedText value) {
    }
}
