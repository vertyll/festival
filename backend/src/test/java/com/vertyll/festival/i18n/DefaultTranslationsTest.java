package com.vertyll.festival.i18n;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.vertyll.festival.common.LocalizedText;

import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTranslationsTest {

    @Test
    void everyLanguageHasTheSameValidIcuMessages() {
        Map<String, LocalizedText> translations = DefaultTranslations.load(JsonMapper.builder().build());

        assertThat(translations).isNotEmpty().containsKey("validation.tooLong");
    }

    @Test
    void icuSyntaxIsChecked() {
        assertThat(IcuMessages.isValid("{count, plural, one {# plik} few {# pliki} other {# plików}}")).isTrue();
        assertThat(IcuMessages.isValid("{count, plural, one {# plik}")).isFalse();
    }
}
