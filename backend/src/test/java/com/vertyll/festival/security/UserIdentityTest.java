package com.vertyll.festival.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserIdentityTest {

    @Test
    void anAccountWithoutAFullNameShowsItsUsername() {
        assertThat(UserIdentity.nameOf(null, "ada")).isEqualTo("ada");
    }

    @Test
    void aFullNameComesBeforeTheUsername() {
        assertThat(UserIdentity.nameOf("Ada Lovelace", "ada")).isEqualTo("Ada Lovelace");
    }
}
