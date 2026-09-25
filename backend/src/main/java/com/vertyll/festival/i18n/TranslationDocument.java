package com.vertyll.festival.i18n;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.vertyll.festival.common.LocalizedText;

@Document("translations")
record TranslationDocument(
    @Id String key,
    LocalizedText messages,
    LocalizedText defaults,
    boolean customized,
    Instant updatedAt
) {

    TranslationDocument customize(LocalizedText newMessages, Instant now) {
        return new TranslationDocument(key, newMessages, defaults, !newMessages.equals(defaults), now);
    }

    TranslationDocument reset(Instant now) {
        return new TranslationDocument(key, defaults, defaults, false, now);
    }
}
