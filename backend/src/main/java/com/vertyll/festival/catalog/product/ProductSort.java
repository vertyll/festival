package com.vertyll.festival.catalog.product;

import org.springframework.data.domain.Sort;

enum ProductSort {
    NEWEST,
    PRICE_ASC,
    PRICE_DESC;

    Sort toSort() {
        return switch (this) {
            case NEWEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case PRICE_ASC -> Sort.by(Sort.Direction.ASC, "price");
            case PRICE_DESC -> Sort.by(Sort.Direction.DESC, "price");
        };
    }
}
