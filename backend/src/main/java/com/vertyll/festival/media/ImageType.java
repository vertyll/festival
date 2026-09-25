package com.vertyll.festival.media;

import java.util.Arrays;
import java.util.Optional;

enum ImageType {
    JPEG("jpg", "image/jpeg") {
        @Override
        boolean matches(byte[] content) {
            return startsWith(content, 0xFF, 0xD8, 0xFF);
        }
    },
    PNG("png", "image/png") {
        @Override
        boolean matches(byte[] content) {
            return startsWith(content, 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A);
        }
    },
    GIF("gif", "image/gif") {
        @Override
        boolean matches(byte[] content) {
            return startsWith(content, 'G', 'I', 'F', '8', '7', 'a')
                    || startsWith(content, 'G', 'I', 'F', '8', '9', 'a');
        }
    },
    WEBP("webp", "image/webp") {
        @Override
        boolean matches(byte[] content) {
            return startsWith(content, 'R', 'I', 'F', 'F') && content.length >= 12 && content[8] == 'W'
                    && content[9] == 'E' && content[10] == 'B' && content[11] == 'P';
        }
    };

    private final String fileExtension;
    private final String contentType;

    ImageType(String extension, String mediaType) {
        this.fileExtension = extension;
        this.contentType = mediaType;
    }

    abstract boolean matches(byte[] content);

    String extension() {
        return fileExtension;
    }

    String mediaType() {
        return contentType;
    }

    static Optional<ImageType> detect(byte[] content) {
        return Arrays.stream(values()).filter(type -> type.matches(content)).findFirst();
    }

    private static boolean startsWith(byte[] content, int... signature) {
        if (content.length < signature.length) {
            return false;
        }
        for (int i = 0; i < signature.length; i++) {
            if ((content[i] & 0xFF) != signature[i]) {
                return false;
            }
        }
        return true;
    }
}
