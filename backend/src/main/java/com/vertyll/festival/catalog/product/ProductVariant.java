package com.vertyll.festival.catalog.product;

import java.util.List;

record ProductVariant(List<String> valueCodes, int stock) {

    ProductVariant {
        valueCodes = List.copyOf(valueCodes);
    }
}
