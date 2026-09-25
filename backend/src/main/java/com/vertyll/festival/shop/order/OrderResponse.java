package com.vertyll.festival.shop.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.bson.types.ObjectId;

import com.vertyll.festival.shop.ShippingDetails;

record OrderResponse(
    ObjectId id,
    OrderStatus status,
    List<OrderLine> lines,
    ShippingDetails shipping,
    BigDecimal itemsTotal,
    BigDecimal shippingPrice,
    BigDecimal total,
    String currency,
    Instant createdAt
) {
}
