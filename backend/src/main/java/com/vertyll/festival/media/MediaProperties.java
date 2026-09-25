package com.vertyll.festival.media;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("festival.media")
record MediaProperties(
    @NotBlank String endpoint,
    @NotBlank String region,
    @NotBlank String bucket,
    @NotBlank String accessKey,
    @NotBlank String secretKey,
    @NotBlank @Pattern(
        regexp = "https?://.+[^/]",
        message = "must be an http(s) URL without a trailing slash"
    ) String publicBaseUrl,
    @NotNull DataSize maxFileSize,
    @NotNull @Positive Integer maxFilesPerUpload,
    @Nullable String sslBundle
) {

    String publicUrl(String key) {
        return publicBaseUrl + "/" + key;
    }

    boolean isPublicUrl(String url) {
        return url.startsWith(publicBaseUrl + "/") && url.length() > publicBaseUrl.length() + 1;
    }
}
