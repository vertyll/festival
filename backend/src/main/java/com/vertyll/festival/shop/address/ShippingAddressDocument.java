package com.vertyll.festival.shop.address;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.vertyll.festival.shop.ShippingDetails;

@Document("shipping_addresses")
record ShippingAddressDocument(@Id String customerId, ShippingDetails details, Instant updatedAt) {
}
