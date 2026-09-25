package com.vertyll.festival.i18n;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import com.vertyll.festival.IntegrationTest;
import com.vertyll.festival.common.MessageKeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.vertyll.festival.TestUsers.admin;

@IntegrationTest
class TranslationsIT {

    private static final String REQUIRED_PATH = "/api/admin/translations/validation.required";
    private static final String TOO_LONG_PATH = "/api/admin/translations/validation.tooLong";
    private static final String EXPORT_PATH = "/api/admin/translations/export";
    private static final String IMPORT_PATH = "/api/admin/translations/import";

    @Autowired
    MockMvc mvc;

    @Test
    void messagesAreServedPerLanguage() throws Exception {
        mvc.perform(get("/api/i18n/en"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$['validation.required']").isString());
        mvc.perform(get("/api/i18n/xx")).andExpect(status().isNotFound());
    }

    @Test
    void administratorCustomizesAndResetsMessage() throws Exception {
        mvc.perform(update(REQUIRED_PATH, "Pole wymagane", "{count, plural, one {x}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors['messages.en'].code").value(MessageKeys.ICU_INVALID));

        mvc.perform(update(REQUIRED_PATH, "Pole wymagane", "Required"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customized").value(true))
            .andExpect(jsonPath("$.defaults.en").value("This field is required."));
        mvc.perform(get("/api/i18n/en")).andExpect(jsonPath("$['validation.required']").value("Required"));

        mvc.perform(delete(REQUIRED_PATH + "/customization").with(admin()).with(csrf()))
            .andExpect(status().isNoContent());
        mvc.perform(get("/api/i18n/en"))
            .andExpect(jsonPath("$['validation.required']").value("This field is required."));
    }

    @Test
    void placeholdersMissingFromDefaultMessageAreRejected() throws Exception {
        mvc.perform(update(TOO_LONG_PATH, "Maksymalnie {max} znaków", "At most {max} characters, {unknown}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(MessageKeys.TRANSLATION_UNKNOWN_PLACEHOLDERS))
            .andExpect(jsonPath("$.args.language").value("en"))
            .andExpect(jsonPath("$.args.placeholders").value("{unknown}"));
    }

    @Test
    void spreadsheetExportsEveryKeyAndImportUpdatesOnlyExistingOnes() throws Exception {
        byte[] exported = mvc.perform(get(EXPORT_PATH).with(admin()))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, TranslationSpreadsheet.CONTENT_TYPE))
            .andReturn()
            .getResponse()
            .getContentAsByteArray();
        assertThat(TranslationSpreadsheet.read(new ByteArrayInputStream(exported))).extracting(TranslationRow::key)
            .contains("validation.required", "common.back");

        mvc.perform(
            importFile(
                TranslationSpreadsheetTest.sheet(
                    new String[] {
                        "key",
                        "pl",
                        "en"
                    },
                    new String[] {
                        "validation.required",
                        "Wymagane",
                        "Required"
                    },
                    new String[] {
                        "new.key",
                        "Nowy",
                        "New"
                    }
                )
            )
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(MessageKeys.TRANSLATION_IMPORT_UNKNOWN_KEY))
            .andExpect(jsonPath("$.args.row").value(3))
            .andExpect(jsonPath("$.args.key").value("new.key"));
        mvc.perform(get("/api/i18n/en"))
            .andExpect(jsonPath("$['validation.required']").value("This field is required."))
            .andExpect(jsonPath("$['new.key']").doesNotExist());

        mvc.perform(
            importFile(
                TranslationSpreadsheetTest.sheet(
                    new String[] {
                        "key",
                        "pl",
                        "en"
                    },
                    new String[] {
                        "validation.required",
                        "Wymagane",
                        "Required"
                    },
                    new String[] {
                        "common.back",
                        "Wróć",
                        "Back"
                    }
                )
            )
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.updated").value(1))
            .andExpect(jsonPath("$.unchanged").value(1));
        mvc.perform(get("/api/i18n/pl")).andExpect(jsonPath("$['validation.required']").value("Wymagane"));

        mvc.perform(importFile(exported)).andExpect(status().isOk()).andExpect(jsonPath("$.updated").value(1));
        mvc.perform(get("/api/admin/translations").with(admin()))
            .andExpect(jsonPath("$[?(@.key == 'validation.required')].customized").value(false));
    }

    private static RequestBuilder importFile(byte[] content) {
        return multipart(IMPORT_PATH)
            .file(new MockMultipartFile("file", "translations.xlsx", TranslationSpreadsheet.CONTENT_TYPE, content))
            .with(admin())
            .with(csrf());
    }

    private static RequestBuilder update(String path, String pl, String en) {
        return put(path).with(admin())
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"messages\":{\"pl\":\"" + pl + "\",\"en\":\"" + en + "\"}}");
    }
}
