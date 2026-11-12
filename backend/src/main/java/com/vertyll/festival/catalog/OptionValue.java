package com.vertyll.festival.catalog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vertyll.festival.common.InvalidRequestException;
import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.MessageKeys;

public record OptionValue(String code, LocalizedText label) {

    public static void requireUniqueCodes(List<OptionValue> values) {
        Set<String> codes = new HashSet<>();
        for (OptionValue value : values) {
            if (!codes.add(value.code())) {
                throw new InvalidRequestException(MessageKeys.OPTION_VALUE_DUPLICATED);
            }
        }
    }
}
