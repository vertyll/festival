package com.vertyll.festival.shop;

public record ShippingDetails(
    String name,
    String email,
    String streetAddress,
    String postalCode,
    String city,
    String country
) {
}
