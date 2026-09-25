package com.vertyll.festival.media;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.vertyll.festival.common.InvalidRequestException;
import com.vertyll.festival.common.MessageKeys;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
class ImageUploadService {

    private static final String CACHE_CONTROL = "public, max-age=31536000, immutable";

    private final S3Client s3;
    private final MediaProperties properties;
    private final Clock clock;

    List<String> upload(List<MultipartFile> files) {
        if (files.isEmpty()) {
            throw new InvalidRequestException(MessageKeys.MEDIA_NO_FILES);
        }
        if (files.size() > properties.maxFilesPerUpload()) {
            throw new InvalidRequestException(
                MessageKeys.MEDIA_TOO_MANY_FILES,
                Map.of("max", properties.maxFilesPerUpload())
            );
        }
        List<ValidatedImage> images = files.stream().map(this::validate).toList();
        return images.stream().map(this::store).toList();
    }

    private ValidatedImage validate(MultipartFile file) {
        if (file.isEmpty() || file.getSize() > properties.maxFileSize().toBytes()) {
            throw new InvalidRequestException(
                MessageKeys.MEDIA_FILE_TOO_LARGE,
                Map.of("maxMegabytes", properties.maxFileSize().toMegabytes())
            );
        }
        byte[] content = read(file);
        ImageType type = ImageType.detect(content)
            .orElseThrow(() -> new InvalidRequestException(MessageKeys.MEDIA_UNSUPPORTED_TYPE));
        return new ValidatedImage(ByteBuffer.wrap(content).asReadOnlyBuffer(), type);
    }

    private String store(ValidatedImage image) {
        String key = newKey(image.type());
        s3.putObject(
            PutObjectRequest.builder()
                .bucket(properties.bucket())
                .key(key)
                .contentType(image.type().mediaType())
                .contentLength((long) image.content().remaining())
                .cacheControl(CACHE_CONTROL)
                .build(),
            RequestBody.fromByteBuffer(image.content())
        );
        return properties.publicUrl(key);
    }

    private String newKey(ImageType type) {
        LocalDate today = LocalDate.now(clock);
        return String.format(
            Locale.ROOT,
            "uploads/%d/%02d/%s.%s",
            today.getYear(),
            today.getMonthValue(),
            UUID.randomUUID(),
            type.extension()
        );
    }

    private static byte[] read(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read the uploaded file", e);
        }
    }

    private record ValidatedImage(ByteBuffer content, ImageType type) {
    }
}
