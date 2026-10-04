package com.vertyll.festival.security;

import jakarta.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("application.redis")
record RedisKeyProperties(@NotBlank String keyPrefix) {
}
