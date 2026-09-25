package com.vertyll.festival.i18n;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.vertyll.festival.common.InvalidRequestException;
import com.vertyll.festival.common.Language;
import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.MessageKeys;

final class TranslationSpreadsheet {

    static final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static final String FILE_NAME = "translations.xlsx";

    private static final String SHEET_NAME = "translations";
    private static final int KEY_COLUMN = 0;
    private static final int KEY_COLUMN_WIDTH = 50 * 256;
    private static final int MESSAGE_COLUMN_WIDTH = 80 * 256;
    private static final List<String> HEADERS =
            Stream.concat(Stream.of("key"), Arrays.stream(Language.values()).map(Language::code)).toList();

    private TranslationSpreadsheet() {
    }

    static byte[] write(List<TranslationDocument> translations) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(SHEET_NAME);
            writeHeader(workbook, sheet);
            for (TranslationDocument translation : translations) {
                Row row = sheet.createRow(sheet.getLastRowNum() + 1);
                row.createCell(KEY_COLUMN).setCellValue(translation.key());
                for (Language language : Language.values()) {
                    row.createCell(column(language)).setCellValue(translation.messages().in(language));
                }
            }
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void writeHeader(Workbook workbook, Sheet sheet) {
        Font bold = workbook.createFont();
        bold.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFont(bold);
        Row header = sheet.createRow(0);
        for (int column = 0; column < HEADERS.size(); column++) {
            header.createCell(column).setCellValue(HEADERS.get(column));
            header.getCell(column).setCellStyle(style);
            sheet.setColumnWidth(column, column == KEY_COLUMN ? KEY_COLUMN_WIDTH : MESSAGE_COLUMN_WIDTH);
        }
        sheet.createFreezePane(1, 1);
        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, HEADERS.size() - 1));
    }

    static List<TranslationRow> read(InputStream input) {
        try (Workbook workbook = open(input)) {
            return rows(workbook.getSheetAt(0));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static List<TranslationRow> rows(Sheet sheet) {
        DataFormatter formatter = new DataFormatter();
        Row header = sheet.getRow(0);
        if (header == null || !HEADERS.equals(cells(header, formatter))) {
            throw new InvalidRequestException(
                MessageKeys.TRANSLATION_IMPORT_INVALID_HEADER,
                Map.of("expected", String.join(", ", HEADERS))
            );
        }
        List<TranslationRow> rows = new ArrayList<>();
        for (Row row : sheet) {
            List<String> cells = cells(row, formatter);
            if (row.getRowNum() > 0 && cells.stream().anyMatch(cell -> !cell.isEmpty())) {
                rows.add(toRow(row.getRowNum() + 1, cells));
            }
        }
        return rows;
    }

    private static Workbook open(InputStream input) {
        try {
            return new XSSFWorkbook(input);
        } catch (IOException | IllegalArgumentException | POIXMLException e) {
            throw new InvalidRequestException(MessageKeys.TRANSLATION_IMPORT_INVALID_FILE, e);
        }
    }

    private static int column(Language language) {
        return HEADERS.indexOf(language.code());
    }

    private static List<String> cells(Row row, DataFormatter formatter) {
        return IntStream.range(0, HEADERS.size())
            .mapToObj(
                column -> formatter.formatCellValue(row.getCell(column, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK))
                    .strip()
            )
            .toList();
    }

    private static TranslationRow toRow(int rowNumber, List<String> cells) {
        String key = cells.get(KEY_COLUMN);
        for (Language language : Language.values()) {
            if (cells.get(column(language)).isEmpty()) {
                throw new InvalidRequestException(
                    MessageKeys.TRANSLATION_IMPORT_EMPTY_MESSAGE,
                    Map.of("row", rowNumber, "key", key, "language", language.code())
                );
            }
        }
        return new TranslationRow(rowNumber, key, LocalizedText.from(language -> cells.get(column(language))));
    }
}
