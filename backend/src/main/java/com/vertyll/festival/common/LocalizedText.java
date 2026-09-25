package com.vertyll.festival.common;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public record LocalizedText(String pl, String en) {

    public LocalizedText {
        pl = pl.strip();
        en = en.strip();
    }

    public static LocalizedText from(Function<Language, String> text) {
        return new LocalizedText(text.apply(Language.PL), text.apply(Language.EN));
    }

    public String in(Language language) {
        return switch (language) {
            case PL -> pl;
            case EN -> en;
        };
    }

    public static List<String> fieldPaths(String field) {
        return Arrays.stream(Language.values()).map(language -> field + "." + language.code()).toList();
    }

    public static String defaultLanguagePath(String field) {
        return field + "." + Language.DEFAULT.code();
    }
}
