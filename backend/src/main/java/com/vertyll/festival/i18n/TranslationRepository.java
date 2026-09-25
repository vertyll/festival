package com.vertyll.festival.i18n;

import org.springframework.data.mongodb.repository.MongoRepository;

interface TranslationRepository extends MongoRepository<TranslationDocument, String> {
}
