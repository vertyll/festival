package com.vertyll.festival.catalog.product;

import java.util.List;

import com.vertyll.festival.catalog.OptionValueResponse;
import com.vertyll.festival.common.LocalizedText;

record ProductOptionResponse(String code, LocalizedText name, List<OptionValueResponse> values) {
}
