package com.vertyll.festival.catalog.product;

import org.springframework.data.domain.Sort;

enum ProductSort {
    NEWEST(Sort.by(Sort.Direction.DESC, "createdAt")),
    PRICE_ASC(Sort.by(Sort.Direction.ASC, "price")),
    PRICE_DESC(Sort.by(Sort.Direction.DESC, "price"));

    private final Sort sort;

    ProductSort(Sort sort) {
        this.sort = sort;
    }

    Sort toSort() {
        return sort;
    }
}
