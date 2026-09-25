package com.vertyll.festival.catalog.product;

import java.io.Serial;
import java.util.Map;

import com.vertyll.festival.common.ConflictException;
import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.MessageKeys;

public final class InsufficientStockException extends ConflictException {

    @Serial
    private static final long serialVersionUID = 1L;

    InsufficientStockException(LocalizedText productName) {
        super(MessageKeys.PRODUCT_INSUFFICIENT_STOCK, Map.of("product", productName));
    }
}
