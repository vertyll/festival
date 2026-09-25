package com.vertyll.festival.administrator;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.vertyll.festival.IntegrationTest;
import com.vertyll.festival.common.MessageKeys;

import com.jayway.jsonpath.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.vertyll.festival.TestUsers.admin;

@IntegrationTest
class AdministratorIT {

    private static final String BOOTSTRAP_ADMIN = "bootstrap-admin@example.com";

    @Autowired
    MockMvc mvc;

    @Test
    void emailIsNormalizedAndMustBeUnique() throws Exception {
        String email = "Admin-" + UUID.randomUUID() + "@Example.com";

        mvc.perform(asAdmin(post("/api/admin/administrators")).content(body(email)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value(email.toLowerCase(Locale.ROOT)));
        mvc.perform(asAdmin(post("/api/admin/administrators")).content(body(email.toUpperCase(Locale.ROOT))))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value(MessageKeys.ADMINISTRATOR_EXISTS));
    }

    @Test
    void administratorCannotRenameOrRemoveThemselves() throws Exception {
        String self = idOf(BOOTSTRAP_ADMIN);

        mvc.perform(asAdmin(put("/api/admin/administrators/" + self)).content(body("other@example.com")))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value(MessageKeys.ADMINISTRATOR_SELF_UPDATE));
        mvc.perform(asAdmin(delete("/api/admin/administrators/" + self)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value(MessageKeys.ADMINISTRATOR_SELF_DELETE));
    }

    @Test
    void otherAdministratorCanBeRemoved() throws Exception {
        String email = "removable-" + UUID.randomUUID() + "@example.com";
        mvc.perform(asAdmin(post("/api/admin/administrators")).content(body(email))).andExpect(status().isCreated());

        mvc.perform(asAdmin(delete("/api/admin/administrators/" + idOf(email)))).andExpect(status().isNoContent());
        mvc.perform(asAdmin(get("/api/admin/administrators")))
            .andExpect(jsonPath("$[?(@.email == '%s')]".formatted(email)).isEmpty());
    }

    private String idOf(String email) {
        MvcTestResult result = MockMvcTester.create(mvc).perform(asAdmin(get("/api/admin/administrators")));
        assertThat(result).hasStatusOk();
        String json = new String(result.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
        List<String> ids = JsonPath.read(json, "$[?(@.email == '%s')].id".formatted(email));
        return ids.getFirst();
    }

    private static String body(String email) {
        return "{\"email\":\"%s\"}".formatted(email);
    }

    private static MockHttpServletRequestBuilder asAdmin(MockHttpServletRequestBuilder request) {
        return request.with(admin()).with(csrf()).contentType(MediaType.APPLICATION_JSON);
    }
}
