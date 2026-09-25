package com.vertyll.festival.i18n;

import java.time.Instant;

import com.vertyll.festival.common.LocalizedText;

record TranslationResponse(
    String key,
    LocalizedText messages,
    LocalizedText defaults,
    boolean customized,
    Instant updatedAt
) {
}
