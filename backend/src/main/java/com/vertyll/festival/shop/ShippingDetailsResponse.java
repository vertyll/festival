package com.vertyll.festival.shop;

public record ShippingDetailsResponse(
    String name,
    String email,
    String streetAddress,
    String postalCode,
    String city,
    String country
) {
}
