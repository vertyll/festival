package com.vertyll.festival.shop;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.ValidationLimits;

public record ShippingDetails(
    @NotBlank(
        message = MessageKeys.REQUIRED
    ) @Size(max = ValidationLimits.NAME_MAX_LENGTH, message = MessageKeys.TOO_LONG) String name,
    @NotBlank(message = MessageKeys.REQUIRED) @Email(
        message = MessageKeys.EMAIL_INVALID
    ) @Size(max = ValidationLimits.EMAIL_MAX_LENGTH, message = MessageKeys.EMAIL_INVALID) String email,
    @NotBlank(
        message = MessageKeys.REQUIRED
    ) @Size(max = ValidationLimits.STREET_ADDRESS_MAX_LENGTH, message = MessageKeys.TOO_LONG) String streetAddress,
    @NotBlank(
        message = MessageKeys.REQUIRED
    ) @Size(max = ValidationLimits.POSTAL_CODE_MAX_LENGTH, message = MessageKeys.TOO_LONG) String postalCode,
    @NotBlank(
        message = MessageKeys.REQUIRED
    ) @Size(max = ValidationLimits.NAME_MAX_LENGTH, message = MessageKeys.TOO_LONG) String city,
    @NotBlank(
        message = MessageKeys.REQUIRED
    ) @Size(max = ValidationLimits.NAME_MAX_LENGTH, message = MessageKeys.TOO_LONG) String country
) {

    public ShippingDetails {
        name = name.strip();
        email = email.strip();
        streetAddress = streetAddress.strip();
        postalCode = postalCode.strip();
        city = city.strip();
        country = country.strip();
    }
}
