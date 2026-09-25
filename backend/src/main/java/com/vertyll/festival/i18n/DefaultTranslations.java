package com.vertyll.festival.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.springframework.core.io.ClassPathResource;

import com.vertyll.festival.common.Language;
import com.vertyll.festival.common.LocalizedText;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

final class DefaultTranslations {

    private static final Pattern KEY_SEGMENT = Pattern.compile("\\w+");
    private static final TypeReference<Map<String, String>> MESSAGES = new TypeReference<>() {
    };

    private DefaultTranslations() {
    }

    static Map<String, LocalizedText> load(ObjectMapper mapper) {
        Map<Language, Map<String, String>> byLanguage = new EnumMap<>(Language.class);
        for (Language language : Language.values()) {
            byLanguage.put(language, read(mapper, language));
        }
        Map<String, String> defaults = byLanguage.get(Language.DEFAULT);
        if (defaults == null) {
            throw new IllegalStateException("Missing translations for the default language");
        }
        Set<String> keys = new TreeSet<>(defaults.keySet());
        requireSameKeys(keys, byLanguage);
        requireValidKeys(keys);
        Map<String, LocalizedText> translations = new TreeMap<>();
        for (String key : keys) {
            translations.put(key, localized(key, byLanguage));
        }
        return translations;
    }

    private static Map<String, String> read(ObjectMapper mapper, Language language) {
        ClassPathResource resource = new ClassPathResource("i18n/" + language.code() + ".json");
        try (InputStream input = resource.getInputStream()) {
            return mapper.readValue(input, MESSAGES);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load " + resource.getPath(), e);
        }
    }

    private static void requireSameKeys(Set<String> keys, Map<Language, Map<String, String>> byLanguage) {
        byLanguage.forEach((language, messages) -> {
            if (!messages.keySet().equals(keys)) {
                throw new IllegalStateException(
                    "Translations " + language.code() + " have different keys than the default language: "
                            + symmetricDifference(keys, messages.keySet())
                );
            }
        });
    }

    private static Set<String> symmetricDifference(Set<String> first, Set<String> second) {
        Set<String> onlyFirst = new TreeSet<>(first);
        onlyFirst.removeAll(second);
        Set<String> onlySecond = new TreeSet<>(second);
        onlySecond.removeAll(first);
        onlyFirst.addAll(onlySecond);
        return onlyFirst;
    }

    private static boolean isValidKey(String key) {
        return Arrays.stream(key.split("\\.", -1)).allMatch(segment -> KEY_SEGMENT.matcher(segment).matches());
    }

    private static void requireValidKeys(Set<String> keys) {
        List<String> malformed = keys.stream().filter(key -> !isValidKey(key)).toList();
        if (!malformed.isEmpty()) {
            throw new IllegalStateException("Malformed translation keys: " + malformed);
        }
        List<String> prefixes =
                keys.stream().filter(key -> keys.stream().anyMatch(other -> other.startsWith(key + "."))).toList();
        if (!prefixes.isEmpty()) {
            throw new IllegalStateException("Translation keys that are prefixes of other keys: " + prefixes);
        }
    }

    private static LocalizedText localized(String key, Map<Language, Map<String, String>> byLanguage) {
        LocalizedText text = LocalizedText.from(language -> message(key, byLanguage, language));
        for (Language language : Language.values()) {
            if (!IcuMessages.isValid(text.in(language))) {
                throw new IllegalStateException(
                    "Invalid ICU message " + key + " (" + language.code() + "): " + text.in(language)
                );
            }
        }
        return text;
    }

    private static String message(String key, Map<Language, Map<String, String>> byLanguage, Language language) {
        Map<String, String> messages = byLanguage.get(language);
        String message = messages == null ? null : messages.get(key);
        if (message == null || message.isBlank()) {
            throw new IllegalStateException("Missing translation " + key + " (" + language.code() + ")");
        }
        return message;
    }
}
