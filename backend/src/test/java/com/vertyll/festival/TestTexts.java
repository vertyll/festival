package com.vertyll.festival;

import java.util.Arrays;
import java.util.List;

import com.vertyll.festival.catalog.OptionValue;
import com.vertyll.festival.common.LocalizedText;

public final class TestTexts {

    private TestTexts() {
    }

    public static LocalizedText text(String polish) {
        return new LocalizedText(polish, polish + " (en)");
    }

    public static List<OptionValue> values(String... codes) {
        return Arrays.stream(codes).map(code -> new OptionValue(code, text(code))).toList();
    }
}
