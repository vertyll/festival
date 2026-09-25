package com.vertyll.festival.catalog.product;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.vertyll.festival.common.InvalidRequestException;
import com.vertyll.festival.common.MessageKeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static com.vertyll.festival.TestTexts.text;
import static com.vertyll.festival.TestTexts.values;

class ProductVariantsTest {

    private static final ProductOption SIZE = new ProductOption("size", text("Rozmiar"), values("s", "m"));
    private static final ProductOption COLOR = new ProductOption("color", text("Kolor"), values("black", "white"));

    @Test
    void productWithoutOptionsHasExactlyOneVariant() {
        List<ProductVariant> variants = ProductVariants.validate(List.of(), List.of(new ProductVariant(List.of(), 7)));

        assertThat(variants).containsExactly(new ProductVariant(List.of(), 7));
    }

    @Test
    void variantsAreReturnedInCartesianOrder() {
        List<ProductVariant> variants = ProductVariants.validate(
            List.of(SIZE, COLOR),
            List.of(
                new ProductVariant(List.of("m", "white"), 4),
                new ProductVariant(List.of("s", "black"), 1),
                new ProductVariant(List.of("m", "black"), 3),
                new ProductVariant(List.of("s", "white"), 2)
            )
        );

        assertThat(variants).extracting(
            ProductVariant::valueCodes
        ).containsExactly(List.of("s", "black"), List.of("s", "white"), List.of("m", "black"), List.of("m", "white"));
    }

    @Test
    void missingCombinationIsRejected() {
        assertThatThrownBy(() -> ProductVariants.validate(List.of(SIZE), List.of(new ProductVariant(List.of("s"), 1))))
            .isInstanceOf(InvalidRequestException.class)
            .hasMessage(MessageKeys.PRODUCT_VARIANTS_MISMATCH);
    }

    @Test
    void unknownCombinationIsRejected() {
        assertThatThrownBy(
            () -> ProductVariants.validate(
                List.of(SIZE),
                List.of(
                    new ProductVariant(List.of("s"), 1),
                    new ProductVariant(List.of("m"), 1),
                    new ProductVariant(List.of("xl"), 1)
                )
            )
        ).isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void duplicatedCombinationIsRejected() {
        assertThatThrownBy(
            () -> ProductVariants.validate(
                List.of(SIZE),
                List.of(
                    new ProductVariant(List.of("s"), 1),
                    new ProductVariant(List.of("s"), 2),
                    new ProductVariant(List.of("m"), 1)
                )
            )
        ).isInstanceOf(InvalidRequestException.class).hasMessage(MessageKeys.PRODUCT_VARIANT_DUPLICATED);
    }

    @Test
    void duplicatedOptionCodeIsRejected() {
        assertThatThrownBy(
            () -> ProductVariants
                .validate(List.of(SIZE, new ProductOption("size", text("Rozmiar"), values("l"))), List.of())
        ).isInstanceOf(InvalidRequestException.class).hasMessage(MessageKeys.PRODUCT_OPTION_DUPLICATED);
    }

    @Test
    void duplicatedValueCodeIsRejected() {
        assertThatThrownBy(
            () -> ProductVariants
                .validate(List.of(new ProductOption("size", text("Rozmiar"), values("s", "s"))), List.of())
        ).isInstanceOf(InvalidRequestException.class).hasMessage(MessageKeys.OPTION_VALUE_DUPLICATED);
    }

    @Test
    void combinationLimitProtectsAgainstHugeProducts() {
        List<ProductOption> options = List.of(
            new ProductOption("a", text("A"), values("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")),
            new ProductOption("b", text("B"), values("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")),
            new ProductOption("c", text("C"), values("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"))
        );

        assertThatThrownBy(() -> ProductVariants.combinations(options)).isInstanceOf(InvalidRequestException.class)
            .hasMessage(MessageKeys.PRODUCT_TOO_MANY_VARIANTS);
    }
}
