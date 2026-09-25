package com.vertyll.festival.i18n;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import com.vertyll.festival.common.InvalidRequestException;
import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.MessageKeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TranslationSpreadsheetTest {

    private static final LocalizedText BACK = new LocalizedText("Wróć", "Back");

    @Test
    void exportedFileIsReadBack() {
        byte[] file = TranslationSpreadsheet
            .write(List.of(new TranslationDocument("common.back", BACK, BACK, false, Instant.EPOCH)));

        assertThat(TranslationSpreadsheet.read(new ByteArrayInputStream(file)))
            .containsExactly(new TranslationRow(2, "common.back", BACK));
    }

    @Test
    void blankRowsAreSkipped() throws IOException {
        byte[] file = sheet(
            new String[] {
                "key",
                "pl",
                "en"
            },
            new String[] {
                "",
                "",
                ""
            },
            new String[] {
                "a.b",
                "{n} x",
                "{n} y"
            }
        );

        assertThat(TranslationSpreadsheet.read(new ByteArrayInputStream(file)))
            .containsExactly(new TranslationRow(3, "a.b", new LocalizedText("{n} x", "{n} y")));
    }

    @Test
    void headerMustListKeyAndEveryLanguage() throws IOException {
        byte[] file = sheet(
            new String[] {
                "key",
                "pl"
            },
            new String[] {
                "a.b",
                "x"
            }
        );

        assertThatThrownBy(() -> TranslationSpreadsheet.read(new ByteArrayInputStream(file)))
            .isInstanceOfSatisfying(InvalidRequestException.class, error -> {
                assertThat(error.messageKey()).isEqualTo(MessageKeys.TRANSLATION_IMPORT_INVALID_HEADER);
                assertThat(error.messageArgs()).containsEntry("expected", "key, pl, en");
            });
    }

    @Test
    void everyLanguageNeedsMessage() throws IOException {
        byte[] file = sheet(
            new String[] {
                "key",
                "pl",
                "en"
            },
            new String[] {
                "a.b",
                "x",
                " "
            }
        );

        assertThatThrownBy(() -> TranslationSpreadsheet.read(new ByteArrayInputStream(file)))
            .isInstanceOfSatisfying(InvalidRequestException.class, error -> {
                assertThat(error.messageKey()).isEqualTo(MessageKeys.TRANSLATION_IMPORT_EMPTY_MESSAGE);
                assertThat(error.messageArgs()).containsEntry("row", 2).containsEntry("language", "en");
            });
    }

    @Test
    void otherFilesAreRejected() {
        byte[] file = "key;pl;en".getBytes(StandardCharsets.UTF_8);

        assertThatThrownBy(() -> TranslationSpreadsheet.read(new ByteArrayInputStream(file))).isInstanceOfSatisfying(
            InvalidRequestException.class,
            error -> assertThat(error.messageKey()).isEqualTo(MessageKeys.TRANSLATION_IMPORT_INVALID_FILE)
        );
    }

    static byte[] sheet(String[]... rows) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet();
            for (int index = 0; index < rows.length; index++) {
                Row row = sheet.createRow(index);
                for (int column = 0; column < rows[index].length; column++) {
                    row.createCell(column).setCellValue(rows[index][column]);
                }
            }
            workbook.write(output);
            return output.toByteArray();
        }
    }
}
