package com.vertyll.festival.common;

import java.util.regex.Pattern;

public final class SearchPatterns {

    private static final Pattern REGEX_METACHARACTERS = Pattern.compile("[\\\\^$.|?*+()\\[\\]{}]");

    private SearchPatterns() {
    }

    public static Pattern containsIgnoreCase(String term) {
        return Pattern.compile(escape(term.strip()), Pattern.CASE_INSENSITIVE);
    }

    static String escape(String value) {
        return REGEX_METACHARACTERS.matcher(value).replaceAll("\\\\$0");
    }
}
