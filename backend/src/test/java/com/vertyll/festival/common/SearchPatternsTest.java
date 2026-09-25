package com.vertyll.festival.common;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SearchPatternsTest {

    @Test
    void metacharactersAreMatchedLiterally() {
        Pattern pattern = SearchPatterns.containsIgnoreCase("(a+)+$");

        assertThat(pattern.matcher("Koszulka (A+)+$ edycja").find()).isTrue();
        assertThat(pattern.matcher("aaaa").find()).isFalse();
    }

    @Test
    void matchingIgnoresCase() {
        assertThat(SearchPatterns.containsIgnoreCase("KUBEK").matcher("kubek festiwalowy").find()).isTrue();
    }
}
