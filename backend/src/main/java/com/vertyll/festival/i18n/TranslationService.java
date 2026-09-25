package com.vertyll.festival.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.vertyll.festival.common.InvalidRequestException;
import com.vertyll.festival.common.Language;
import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.NotFoundException;
import com.vertyll.festival.common.ValidationLimits;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
@Service
@RequiredArgsConstructor
class TranslationService implements SmartInitializingSingleton {

    private static final String KEY = "key";

    private final TranslationRepository repository;
    private final MongoTemplate mongo;
    private final TranslationMapper mapper;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    Map<String, String> messages(Language language) {
        return repository.findAll()
            .stream()
            .collect(
                Collectors.toMap(
                    TranslationDocument::key,
                    translation -> translation.messages().in(language),
                    (first, _) -> first,
                    TreeMap::new
                )
            );
    }

    List<TranslationResponse> findAll() {
        return repository.findAll(Sort.by(KEY)).stream().map(mapper::toResponse).toList();
    }

    TranslationResponse update(String key, TranslationRequest request) {
        TranslationDocument current = find(key);
        for (Language language : Language.values()) {
            Set<String> unknown = unknownPlaceholders(request.messages(), current.defaults(), language);
            if (!unknown.isEmpty()) {
                throw new InvalidRequestException(
                    MessageKeys.TRANSLATION_UNKNOWN_PLACEHOLDERS,
                    Map.of("language", language.code(), "placeholders", String.join(", ", unknown))
                );
            }
        }
        return mapper.toResponse(repository.save(current.customize(request.messages(), clock.instant())));
    }

    void reset(String key) {
        repository.save(find(key).reset(clock.instant()));
    }

    byte[] exportSpreadsheet() {
        return TranslationSpreadsheet.write(repository.findAll(Sort.by(KEY)));
    }

    TranslationImportResponse importSpreadsheet(MultipartFile file) {
        List<TranslationRow> rows = TranslationSpreadsheet.read(inputStream(file));
        Map<String, TranslationDocument> current =
                repository.findAll().stream().collect(Collectors.toMap(TranslationDocument::key, Function.identity()));
        Set<String> seen = new HashSet<>();
        List<TranslationDocument> changed = new ArrayList<>();
        Instant now = clock.instant();
        for (TranslationRow row : rows) {
            Map<String, Object> location = Map.of("row", row.number(), "key", row.key());
            if (!seen.add(row.key())) {
                throw new InvalidRequestException(MessageKeys.TRANSLATION_IMPORT_DUPLICATE_KEY, location);
            }
            TranslationDocument translation = current.get(row.key());
            if (translation == null) {
                throw new InvalidRequestException(MessageKeys.TRANSLATION_IMPORT_UNKNOWN_KEY, location);
            }
            requireImportable(row, translation.defaults());
            if (!translation.messages().equals(row.messages())) {
                changed.add(translation.customize(row.messages(), now));
            }
        }
        repository.saveAll(changed);
        return new TranslationImportResponse(changed.size(), rows.size() - changed.size());
    }

    private static void requireImportable(TranslationRow row, LocalizedText defaults) {
        for (Language language : Language.values()) {
            Map<String, Object> location = Map.of("row", row.number(), "key", row.key(), "language", language.code());
            if (row.messages().in(language).length() > ValidationLimits.DESCRIPTION_MAX_LENGTH) {
                Map<String, Object> args = new HashMap<>(location);
                args.put("max", ValidationLimits.DESCRIPTION_MAX_LENGTH);
                throw new InvalidRequestException(MessageKeys.TRANSLATION_IMPORT_TOO_LONG, args);
            }
            if (!IcuMessages.isValid(row.messages().in(language))) {
                throw new InvalidRequestException(MessageKeys.TRANSLATION_IMPORT_INVALID_MESSAGE, location);
            }
            Set<String> unknown = unknownPlaceholders(row.messages(), defaults, language);
            if (!unknown.isEmpty()) {
                Map<String, Object> args = new HashMap<>(location);
                args.put("placeholders", String.join(", ", unknown));
                throw new InvalidRequestException(MessageKeys.TRANSLATION_IMPORT_UNKNOWN_PLACEHOLDERS, args);
            }
        }
    }

    private static Set<String> unknownPlaceholders(LocalizedText messages, LocalizedText defaults, Language language) {
        Set<String> unknown = new TreeSet<>(IcuMessages.placeholders(messages.in(language)));
        unknown.removeAll(IcuMessages.placeholders(defaults.in(language)));
        return unknown;
    }

    private static InputStream inputStream(MultipartFile file) {
        try {
            return file.getInputStream();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private TranslationDocument find(String key) {
        return repository.findById(key).orElseThrow(() -> new NotFoundException(MessageKeys.TRANSLATION_NOT_FOUND));
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, LocalizedText> defaults = DefaultTranslations.load(objectMapper);
        BulkOperations bulk = mongo.bulkOps(BulkOperations.BulkMode.UNORDERED, TranslationDocument.class);
        defaults.forEach((key, messages) -> {
            bulk.upsert(
                query(where(KEY).is(key)),
                new Update().setOnInsert("messages", messages)
                    .setOnInsert("customized", false)
                    .setOnInsert("updatedAt", clock.instant())
                    .set("defaults", messages)
            );
            bulk.updateOne(
                query(where(KEY).is(key).and("customized").is(false)),
                new Update().set("messages", messages)
            );
        });
        bulk.execute();
        Set<String> keys = defaults.keySet();
        long removed = mongo.remove(query(where(KEY).nin(keys)), TranslationDocument.class).getDeletedCount();
        log.info("[INFO] Translations synchronized: {} keys, {} stale removed", keys.size(), removed);
    }
}
