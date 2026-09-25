package com.vertyll.festival.shop.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.vertyll.festival.shop.ShippingDetails;

@Document("orders")
@CompoundIndex(name = "customer_newest", def = "{'customerId': 1, 'createdAt': -1}")
record OrderDocument(
    @Id ObjectId id,
    String customerId,
    OrderStatus status,
    List<OrderLine> lines,
    ShippingDetails shipping,
    BigDecimal itemsTotal,
    BigDecimal shippingPrice,
    BigDecimal total,
    String currency,
    Instant createdAt
) {

    OrderDocument {
        lines = List.copyOf(lines);
    }

    static OrderDocument place(
        String customerId,
        List<OrderLine> lines,
        ShippingDetails shipping,
        BigDecimal shippingPrice,
        String currency,
        Instant now
    ) {
        BigDecimal itemsTotal = lines.stream().map(OrderLine::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new OrderDocument(
            new ObjectId(),
            customerId,
            OrderStatus.AWAITING_PAYMENT,
            lines,
            shipping,
            itemsTotal,
            shippingPrice,
            itemsTotal.add(shippingPrice),
            currency,
            now
        );
    }
}
