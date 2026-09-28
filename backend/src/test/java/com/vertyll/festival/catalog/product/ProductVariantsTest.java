package com.vertyll.festival.catalog.product;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.vertyll.festival.TestTexts;
import com.vertyll.festival.common.InvalidRequestException;
import com.vertyll.festival.common.MessageKeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductVariantsTest {

    private static final ProductOption SIZE =
            new ProductOption("size", TestTexts.text("Rozmiar"), TestTexts.values("s", "m"));
    private static final ProductOption COLOR =
            new ProductOption("color", TestTexts.text("Kolor"), TestTexts.values("black", "white"));

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
        List<ProductOption> options = List.of(SIZE);
        List<ProductVariant> variants = List.of(new ProductVariant(List.of("s"), 1));

        assertThatThrownBy(() -> ProductVariants.validate(options, variants))
            .isInstanceOf(InvalidRequestException.class)
            .hasMessage(MessageKeys.PRODUCT_VARIANTS_MISMATCH);
    }

    @Test
    void unknownCombinationIsRejected() {
        List<ProductOption> options = List.of(SIZE);
        List<ProductVariant> variants = List.of(
            new ProductVariant(List.of("s"), 1),
            new ProductVariant(List.of("m"), 1),
            new ProductVariant(List.of("xl"), 1)
        );

        assertThatThrownBy(() -> ProductVariants.validate(options, variants))
            .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void duplicatedCombinationIsRejected() {
        List<ProductOption> options = List.of(SIZE);
        List<ProductVariant> variants = List.of(
            new ProductVariant(List.of("s"), 1),
            new ProductVariant(List.of("s"), 2),
            new ProductVariant(List.of("m"), 1)
        );

        assertThatThrownBy(() -> ProductVariants.validate(options, variants))
            .isInstanceOf(InvalidRequestException.class)
            .hasMessage(MessageKeys.PRODUCT_VARIANT_DUPLICATED);
    }

    @Test
    void duplicatedOptionCodeIsRejected() {
        List<ProductOption> options =
                List.of(SIZE, new ProductOption("size", TestTexts.text("Rozmiar"), TestTexts.values("l")));
        List<ProductVariant> variants = List.of();

        assertThatThrownBy(() -> ProductVariants.validate(options, variants))
            .isInstanceOf(InvalidRequestException.class)
            .hasMessage(MessageKeys.PRODUCT_OPTION_DUPLICATED);
    }

    @Test
    void duplicatedValueCodeIsRejected() {
        List<ProductOption> options =
                List.of(new ProductOption("size", TestTexts.text("Rozmiar"), TestTexts.values("s", "s")));
        List<ProductVariant> variants = List.of();

        assertThatThrownBy(() -> ProductVariants.validate(options, variants))
            .isInstanceOf(InvalidRequestException.class)
            .hasMessage(MessageKeys.OPTION_VALUE_DUPLICATED);
    }

    @Test
    void combinationLimitProtectsAgainstHugeProducts() {
        List<ProductOption> options = List.of(
            new ProductOption(
                "a",
                TestTexts.text("A"),
                TestTexts.values("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
            ),
            new ProductOption(
                "b",
                TestTexts.text("B"),
                TestTexts.values("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
            ),
            new ProductOption(
                "c",
                TestTexts.text("C"),
                TestTexts.values("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
            )
        );

        assertThatThrownBy(() -> ProductVariants.combinations(options)).isInstanceOf(InvalidRequestException.class)
            .hasMessage(MessageKeys.PRODUCT_TOO_MANY_VARIANTS);
    }
}
