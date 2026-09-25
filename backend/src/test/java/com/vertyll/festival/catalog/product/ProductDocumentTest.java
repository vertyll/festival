package com.vertyll.festival.catalog.product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import com.vertyll.festival.TestTexts;
import com.vertyll.festival.common.InvalidRequestException;
import com.vertyll.festival.common.MessageKeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductDocumentTest {

    private static final ProductDocument T_SHIRT = new ProductDocument(
        new ObjectId(),
        TestTexts.text("Koszulka"),
        null,
        new BigDecimal("49.99"),
        null,
        List.of(),
        List.of(
            new ProductOption("size", TestTexts.text("Rozmiar"), TestTexts.values("s", "m")),
            new ProductOption("color", TestTexts.text("Kolor"), TestTexts.values("black"))
        ),
        List.of(new ProductVariant(List.of("s", "black"), 2), new ProductVariant(List.of("m", "black"), 5)),
        Instant.EPOCH,
        Instant.EPOCH
    );

    @Test
    void selectionIsOrderedLikeProductOptions() {
        assertThat(T_SHIRT.valueCodesFor(Map.of("color", "black", "size", "m"))).containsExactly("m", "black");
    }

    @Test
    void incompleteSelectionIsRejected() {
        assertThatThrownBy(() -> T_SHIRT.valueCodesFor(Map.of("size", "m"))).isInstanceOf(InvalidRequestException.class)
            .hasMessage(MessageKeys.PRODUCT_OPTIONS_INCOMPLETE);
    }

    @Test
    void valueOutsideOptionIsRejected() {
        assertThatThrownBy(() -> T_SHIRT.valueCodesFor(Map.of("size", "xl", "color", "black")))
            .isInstanceOf(InvalidRequestException.class)
            .hasMessage(MessageKeys.PRODUCT_OPTION_VALUE_INVALID);
    }

    @Test
    void selectionIsDescribedWithLocalizedLabels() {
        assertThat(T_SHIRT.describe(List.of("m", "black"))).containsExactly(
            new SelectedOption(TestTexts.text("Rozmiar"), TestTexts.text("m")),
            new SelectedOption(TestTexts.text("Kolor"), TestTexts.text("black"))
        );
    }

    @Test
    void totalStockSumsAllVariants() {
        assertThat(T_SHIRT.totalStock()).isEqualTo(7);
    }
}
