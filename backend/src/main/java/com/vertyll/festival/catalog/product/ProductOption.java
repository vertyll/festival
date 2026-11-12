package com.vertyll.festival.catalog.product;

import java.util.List;

import com.vertyll.festival.catalog.OptionValue;
import com.vertyll.festival.common.LocalizedText;

record ProductOption(String code, LocalizedText name, List<OptionValue> values) {

    ProductOption {
        values = List.copyOf(values);
    }
}
