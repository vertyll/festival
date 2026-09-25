package com.vertyll.festival.media;

import java.net.URI;

import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration(proxyBeanMethods = false)
class StorageConfig {

    @Bean(destroyMethod = "close")
    S3Client s3Client(MediaProperties properties, SslBundles sslBundles) {
        return S3Client.builder()
            .region(Region.of(properties.region()))
            .endpointOverride(URI.create(properties.endpoint()))
            .forcePathStyle(true)
            .credentialsProvider(
                StaticCredentialsProvider
                    .create(AwsBasicCredentials.create(properties.accessKey(), properties.secretKey()))
            )
            .httpClient(httpClient(properties, sslBundles))
            .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
            .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
            .build();
    }

    private static SdkHttpClient httpClient(MediaProperties properties, SslBundles sslBundles) {
        UrlConnectionHttpClient.Builder builder = UrlConnectionHttpClient.builder();
        String bundleName = properties.sslBundle();
        if (bundleName != null) {
            SslBundle bundle = sslBundles.getBundle(bundleName);
            builder.tlsTrustManagersProvider(() -> bundle.getManagers().getTrustManagers());
        }
        return builder.build();
    }
}
