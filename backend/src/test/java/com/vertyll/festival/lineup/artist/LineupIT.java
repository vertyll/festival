package com.vertyll.festival.lineup.artist;

import java.nio.charset.StandardCharsets;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.vertyll.festival.TestUsers.admin;

@IntegrationTest
class LineupIT {

    @Autowired
    MockMvc mvc;

    @Test
    void artistCannotPlayOnUnknownStage() throws Exception {
        mvc.perform(asAdmin(post("/api/admin/artists")).content(artist(new ObjectId().toHexString())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(MessageKeys.ARTIST_STAGE_NOT_FOUND));
    }

    @Test
    void deletingStageDetachesItsArtists() throws Exception {
        String stage = createdId(post("/api/admin/stages"), "{\"name\":{\"pl\":\"Scena\",\"en\":\"Stage\"}}");
        String artist = createdId(post("/api/admin/artists"), artist(stage));

        mvc.perform(asAdmin(delete("/api/admin/stages/" + stage))).andExpect(status().isNoContent());

        mvc.perform(asAdmin(get("/api/admin/artists/" + artist)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stageId").isEmpty());
        mvc.perform(get("/api/artists/" + artist)).andExpect(status().isOk());
    }

    private String createdId(MockHttpServletRequestBuilder request, String body) {
        MvcTestResult result = MockMvcTester.create(mvc).perform(asAdmin(request).content(body));
        assertThat(result).hasStatus(HttpStatus.CREATED);
        return JsonPath.read(new String(result.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8), "$.id");
    }

    private static String artist(String stageId) {
        return """
                {"name": "Artysta", "description": null, "images": [], "stageId": "%s",
                 "concertDate": "2027-07-01", "concertTime": "21:00"}
                """.formatted(stageId);
    }

    private static MockHttpServletRequestBuilder asAdmin(MockHttpServletRequestBuilder request) {
        return request.with(admin()).with(csrf()).contentType(MediaType.APPLICATION_JSON);
    }
}
