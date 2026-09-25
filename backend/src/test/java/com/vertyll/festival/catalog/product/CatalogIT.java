package com.vertyll.festival.catalog.product;

import java.nio.charset.StandardCharsets;

import org.jspecify.annotations.Nullable;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.vertyll.festival.TestUsers.admin;

@IntegrationTest
class CatalogIT {

    @Autowired
    MockMvc mvc;

    @Test
    void customerSeesProductInNestedCategoryWithStockHiddenPerSettings() throws Exception {
        String clothes = createdId(post("/api/admin/categories"), category("Odzież", null));
        String tShirts = createdId(post("/api/admin/categories"), category("Koszulki", clothes));
        String product = createdId(post("/api/admin/products"), """
                {
                  "name": {"pl": "Koszulka festiwalowa", "en": "Festival T-shirt"},
                  "description": null,
                  "price": 79.90,
                  "categoryId": "%s",
                  "images": ["https://media.example.com/uploads/a.webp"],
                  "options": [{
                    "code": "size",
                    "name": {"pl": "Rozmiar", "en": "Size"},
                    "values": [
                      {"code": "s", "label": {"pl": "S", "en": "S"}},
                      {"code": "m", "label": {"pl": "M", "en": "M"}}
                    ]
                  }],
                  "variants": [
                    {"valueCodes": ["m"], "stock": 0},
                    {"valueCodes": ["s"], "stock": 3}
                  ]
                }
                """.formatted(tShirts));

        mvc.perform(get("/api/products/" + product))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(product))
            .andExpect(jsonPath("$.price").value(79.9))
            .andExpect(jsonPath("$.totalStock").value(3))
            .andExpect(jsonPath("$.name.en").value("Festival T-shirt"))
            .andExpect(jsonPath("$.variants[0].valueCodes[0]").value("s"))
            .andExpect(jsonPath("$.variants[0].available").value(true))
            .andExpect(jsonPath("$.variants[0].stock").isEmpty())
            .andExpect(jsonPath("$.variants[1].available").value(false))
            .andExpect(jsonPath("$.categoryPath[0].name.pl").value("Odzież"))
            .andExpect(jsonPath("$.categoryPath[1].name.en").value("Koszulki (en)"));
    }

    @Test
    void variantsMustCoverEveryCombination() throws Exception {
        mvc.perform(asAdmin(post("/api/admin/products")).content("""
                {
                  "name": {"pl": "Czapka", "en": "Cap"}, "description": null, "price": 10.00,
                  "categoryId": null, "images": [],
                  "options": [{
                    "code": "color",
                    "name": {"pl": "Kolor", "en": "Colour"},
                    "values": [
                      {"code": "black", "label": {"pl": "Czarny", "en": "Black"}},
                      {"code": "white", "label": {"pl": "Biały", "en": "White"}}
                    ]
                  }],
                  "variants": [{"valueCodes": ["black"], "stock": 1}]
                }
                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(MessageKeys.PRODUCT_VARIANTS_MISMATCH));
    }

    @Test
    void priceAndStockHaveUpperLimits() throws Exception {
        mvc.perform(asAdmin(post("/api/admin/products")).content("""
                {
                  "name": {"pl": "Czapka", "en": "Cap"}, "description": null, "price": 100000000.00,
                  "categoryId": null, "images": [], "options": [],
                  "variants": [{"valueCodes": [], "stock": 1000001}]
                }
                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors.price.code").value(MessageKeys.TOO_LARGE))
            .andExpect(jsonPath("$.errors.price.args.value").value("99999999.99"))
            .andExpect(jsonPath("$.errors['variants[0].stock'].code").value(MessageKeys.TOO_LARGE))
            .andExpect(jsonPath("$.errors['variants[0].stock'].args.value").value(1_000_000));
    }

    @Test
    void everyLanguageIsRequiredAndErrorsAreReturnedAsKeys() throws Exception {
        mvc.perform(asAdmin(post("/api/admin/stages")).content("{\"name\":{\"pl\":\"Scena\",\"en\":\" \"}}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(MessageKeys.INVALID_FORM))
            .andExpect(jsonPath("$.errors['name.en'].code").value(MessageKeys.REQUIRED));
        mvc.perform(
            asAdmin(post("/api/admin/stages"))
                .content("{\"name\":{\"pl\":\"Scena\",\"en\":\"%s\"}}".formatted("x".repeat(51)))
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors['name.en'].code").value(MessageKeys.TOO_LONG))
            .andExpect(jsonPath("$.errors['name.en'].args.max").value(50));
    }

    @Test
    void unknownFieldsAreRejected() throws Exception {
        mvc.perform(
            asAdmin(post("/api/admin/stages"))
                .content("{\"name\":{\"pl\":\"Scena\",\"en\":\"Stage\"},\"createdAt\":\"2020-01-01\"}")
        ).andExpect(status().isBadRequest());
    }

    @Test
    void searchTreatsRegexMetacharactersLiterally() throws Exception {
        mvc.perform(get("/api/products").param("term", "(a+)+$[")).andExpect(status().isOk());
    }

    @Test
    void categoryCannotBecomeItsOwnAncestor() throws Exception {
        String parent = createdId(post("/api/admin/categories"), category("A", null));
        String child = createdId(post("/api/admin/categories"), category("B", parent));

        mvc.perform(asAdmin(put("/api/admin/categories/" + parent)).content(category("A", child)))
            .andExpect(status().isBadRequest());
    }

    private String createdId(MockHttpServletRequestBuilder request, String body) {
        MvcTestResult result = MockMvcTester.create(mvc).perform(asAdmin(request).content(body));
        assertThat(result).hasStatus(HttpStatus.CREATED);
        return JsonPath.read(new String(result.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8), "$.id");
    }

    private static String category(String name, @Nullable String parentId) {
        return "{\"name\":{\"pl\":\"%s\",\"en\":\"%s (en)\"},\"parentId\":%s}"
            .formatted(name, name, parentId == null ? "null" : "\"" + parentId + "\"");
    }

    private static MockHttpServletRequestBuilder asAdmin(MockHttpServletRequestBuilder request) {
        return request.with(admin()).with(csrf()).contentType(MediaType.APPLICATION_JSON);
    }
}
