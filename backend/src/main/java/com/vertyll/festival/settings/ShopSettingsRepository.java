package com.vertyll.festival.settings;

import org.springframework.data.mongodb.repository.MongoRepository;

interface ShopSettingsRepository extends MongoRepository<ShopSettingsDocument, String> {
}
