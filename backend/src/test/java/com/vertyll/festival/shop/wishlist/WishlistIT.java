package com.vertyll.festival.shop.wishlist;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.vertyll.festival.IntegrationTest;

import com.jayway.jsonpath.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.vertyll.festival.TestUsers.admin;
import static com.vertyll.festival.TestUsers.customer;

@IntegrationTest
class WishlistIT {

    @Autowired
    MockMvc mvc;

    @Test
    void deletedProductDisappearsFromWishlists() throws Exception {
        String product = createProduct();

        mvc.perform(put("/api/account/wishlist/" + product).with(customer()).with(csrf()))
            .andExpect(status().isNoContent());
        mvc.perform(get("/api/account/wishlist/product-ids").with(customer()))
            .andExpect(jsonPath("$", hasItem(product)));

        mvc.perform(delete("/api/admin/products/" + product).with(admin()).with(csrf()))
            .andExpect(status().isNoContent());

        mvc.perform(get("/api/account/wishlist/product-ids").with(customer()))
            .andExpect(jsonPath("$", not(hasItem(product))));
    }

    @Test
    void customerKeepsTheirShippingAddress() throws Exception {
        String address = """
                {"name": "Jan", "email": "jan@example.com", "streetAddress": "Długa 1", "postalCode": "80-001",
                 "city": "Gdańsk", "country": "Polska"}
                """;

        mvc.perform(
            put("/api/account/address").with(customer())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(address)
        ).andExpect(status().isOk());

        mvc.perform(get("/api/account/address").with(customer()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.city").value("Gdańsk"));
    }

    private String createProduct() {
        MvcTestResult result = MockMvcTester.create(mvc)
            .perform(
                post("/api/admin/products").with(admin())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                {"name": {"pl": "Kubek", "en": "Mug"}, "description": null, "price": 19.99, "categoryId": null,
                                 "images": [], "options": [], "variants": [{"valueCodes": [], "stock": 5}]}
                                """
                    )
            );
        assertThat(result).hasStatus(HttpStatus.CREATED);
        return JsonPath.read(new String(result.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8), "$.id");
    }
}
