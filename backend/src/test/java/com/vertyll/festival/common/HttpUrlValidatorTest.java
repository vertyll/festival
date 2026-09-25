package com.vertyll.festival.common;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class HttpUrlValidatorTest {

    @ParameterizedTest
    @ValueSource(
        strings = {
            "https://sunset-festival.pl",
            "http://example.com/sponsor?id=1"
        }
    )
    void acceptsHttpUrls(String url) {
        assertThat(HttpUrlValidator.isHttpUrl(url)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
            "javascript:alert(1)",
            "data:text/html;base64,PHNjcmlwdD4=",
            "//example.com",
            "ftp://example.com",
            "https://",
            " "
        }
    )
    void rejectsEverythingElse(String url) {
        assertThat(HttpUrlValidator.isHttpUrl(url)).isFalse();
    }
}
