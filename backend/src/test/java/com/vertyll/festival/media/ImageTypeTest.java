package com.vertyll.festival.media;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageTypeTest {

    @Test
    void recognizesSupportedFormatsBySignature() {
        assertThat(
            ImageType.detect(
                new byte[] {
                    (byte) 0xFF,
                    (byte) 0xD8,
                    (byte) 0xFF,
                    0x00
                }
            )
        ).contains(ImageType.JPEG);
        assertThat(
            ImageType.detect(
                new byte[] {
                    (byte) 0x89,
                    'P',
                    'N',
                    'G',
                    0x0D,
                    0x0A,
                    0x1A,
                    0x0A
                }
            )
        ).contains(ImageType.PNG);
        assertThat(ImageType.detect("GIF89a...".getBytes(StandardCharsets.US_ASCII))).contains(ImageType.GIF);
        assertThat(ImageType.detect("RIFF\0\0\0\0WEBPVP8 ".getBytes(StandardCharsets.US_ASCII)))
            .contains(ImageType.WEBP);
    }

    @Test
    void rejectsSvgAndOtherContentRegardlessOfFileName() {
        assertThat(ImageType.detect("<svg onload=alert(1)>".getBytes(StandardCharsets.UTF_8))).isEmpty();
        assertThat(ImageType.detect("%PDF-1.7".getBytes(StandardCharsets.US_ASCII))).isEmpty();
        assertThat(ImageType.detect(new byte[0])).isEmpty();
    }
}
