package com.vertyll.festival.i18n;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IcuMessagesTest {

    @Test
    void collectsArgumentsAndTags() {
        assertThat(IcuMessages.placeholders("<b>{name}</b>: {count, plural, one {# item} other {# items}}"))
            .containsExactly("<b>", "{count}", "{name}");
    }

    @Test
    void recognisesBrokenSyntax() {
        assertThat(IcuMessages.isValid("{count, plural, one {x}")).isFalse();
        assertThat(IcuMessages.isValid("{count, plural, other {# x}}")).isTrue();
    }
}
