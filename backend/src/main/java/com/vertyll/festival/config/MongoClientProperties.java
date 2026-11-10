package com.vertyll.festival.config;

import java.time.Duration;

import jakarta.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** How long the MongoDB client waits to connect, to choose a server and for a reply. */
@Validated
@ConfigurationProperties("application.mongodb")
record MongoClientProperties(
    @NotNull Duration connectTimeout,
    @NotNull Duration readTimeout,
    @NotNull Duration serverSelectionTimeout
) {
}
