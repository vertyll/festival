package com.vertyll.festival.settings;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("festival.shop")
public record ShopProperties(
    @NotNull Boolean checkoutEnabled,
    @NotBlank String currency,
    @Valid @NotNull InitialSettings initialSettings
) {

    public record InitialSettings(
        @NotNull @DecimalMin("0.00") BigDecimal shippingPrice,
        @NotNull Boolean stockVisible,
        @NotNull Boolean variantStockVisible
    ) {
    }
}
