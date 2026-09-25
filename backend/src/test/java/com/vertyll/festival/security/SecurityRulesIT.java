package com.vertyll.festival.security;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.vertyll.festival.IntegrationTest;
import com.vertyll.festival.common.MessageKeys;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.vertyll.festival.TestUsers.admin;
import static com.vertyll.festival.TestUsers.customer;
import static com.vertyll.festival.TestUsers.removedAdmin;

@IntegrationTest
class SecurityRulesIT {

    private static final String STAGE = "{\"name\":{\"pl\":\"Scena główna\",\"en\":\"Main stage\"}}";

    @Autowired
    MockMvc mvc;

    @Nested
    class Anonymous {

        @Test
        void canReadPublicContent() throws Exception {
            mvc.perform(get("/api/products")).andExpect(status().isOk());
            mvc.perform(get("/api/artists")).andExpect(status().isOk());
            mvc.perform(get("/api/news")).andExpect(status().isOk());
            mvc.perform(get("/api/sponsors")).andExpect(status().isOk());
            mvc.perform(get("/api/settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checkoutEnabled").value(true))
                .andExpect(jsonPath("$.currency").value("PLN"));
        }

        @Test
        void seesAnonymousSession() throws Exception {
            mvc.perform(get("/api/me")).andExpect(status().isOk()).andExpect(jsonPath("$.user").isEmpty());
        }

        @Test
        void cannotWriteContent() throws Exception {
            mvc.perform(post("/api/news").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
            mvc.perform(post("/api/admin/news").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        }

        @Test
        void cannotSeeOrdersOrAccountData() throws Exception {
            mvc.perform(get("/api/admin/orders")).andExpect(status().isUnauthorized());
            mvc.perform(get("/api/account/orders")).andExpect(status().isUnauthorized());
            mvc.perform(get("/api/account/address")).andExpect(status().isUnauthorized());
            mvc.perform(get("/api/account/wishlist")).andExpect(status().isUnauthorized());
        }

        @Test
        void cannotPlaceOrders() throws Exception {
            mvc.perform(post("/api/orders").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        }

        @Test
        void unknownEndpointsAreDenied() throws Exception {
            mvc.perform(post("/api/update-availability").with(csrf())).andExpect(status().isUnauthorized());
            mvc.perform(get("/api/hello")).andExpect(status().isUnauthorized());
            mvc.perform(get("/actuator/env")).andExpect(status().isUnauthorized());
        }

        @Test
        void healthIsAvailable() throws Exception {
            mvc.perform(get("/actuator/health")).andExpect(status().isOk());
        }
    }

    @Nested
    class Customer {

        @Test
        void seesOwnSessionWithoutAdministratorRole() throws Exception {
            mvc.perform(get("/api/me").with(customer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("klient@example.com"))
                .andExpect(jsonPath("$.user.administrator").value(false));
        }

        @Test
        void canUseOwnAccount() throws Exception {
            mvc.perform(get("/api/account/orders").with(customer())).andExpect(status().isOk());
            mvc.perform(get("/api/account/wishlist/product-ids").with(customer())).andExpect(status().isOk());
        }

        @Test
        void cannotUseAdminApi() throws Exception {
            mvc.perform(get("/api/admin/orders").with(customer())).andExpect(status().isForbidden());
            mvc.perform(get("/api/admin/administrators").with(customer())).andExpect(status().isForbidden());
            mvc.perform(
                post("/api/admin/stages").with(customer())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(STAGE)
            ).andExpect(status().isForbidden());
        }

        @Test
        void addressAcceptsOnlyKnownFields() throws Exception {
            mvc.perform(
                put("/api/account/address").with(customer())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {"name": "Jan", "email": "jan@example.com", "streetAddress": "Długa 1",
                             "postalCode": "80-001", "city": "Gdańsk", "country": "Polska",
                             "customerId": "ktos-inny"}
                            """)
            ).andExpect(status().isBadRequest());
        }

        @Test
        void addressIsValidated() throws Exception {
            mvc.perform(
                put("/api/account/address").with(customer())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {"name": "", "email": "nie-email", "streetAddress": "Długa 1",
                             "postalCode": "80-001", "city": "Gdańsk", "country": "Polska"}
                            """)
            )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name.code").value(MessageKeys.REQUIRED))
                .andExpect(jsonPath("$.errors.email.code").value(MessageKeys.EMAIL_INVALID));
        }
    }

    @Nested
    class Administrator {

        @Test
        void mutationsRequireCsrfToken() throws Exception {
            mvc.perform(post("/api/admin/stages").with(admin()).contentType(MediaType.APPLICATION_JSON).content(STAGE))
                .andExpect(status().isForbidden());
        }

        @Test
        void canManageContent() throws Exception {
            mvc.perform(
                post("/api/admin/stages").with(admin())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(STAGE)
            )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.name.pl").value("Scena główna"));
        }

        @Test
        void sponsorLinkMustBeHttp() throws Exception {
            mvc.perform(
                post("/api/admin/sponsors").with(admin())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Sponsor\",\"link\":\"javascript:alert(1)\",\"images\":[]}")
            )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.link.code").value(MessageKeys.URL_INVALID));
        }

        @Test
        void imagesMustComeFromFestivalStorage() throws Exception {
            mvc.perform(
                post("/api/admin/news").with(admin()).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
                        {"name": {"pl": "News", "en": "News"}, "description": {"pl": "Treść", "en": "Text"},
                         "images": ["https://tracker.example.org/pixel.gif"]}
                        """)
            )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors['images[0]'].code").value(MessageKeys.MEDIA_URL_INVALID));
        }

        @Test
        void loseAccessAsSoonAsTheyAreRemovedFromAdministrators() throws Exception {
            mvc.perform(get("/api/admin/orders").with(removedAdmin())).andExpect(status().isForbidden());
            mvc.perform(get("/api/me").with(removedAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.administrator").value(false));
        }

        @Test
        void invalidIdentifierIsBadRequest() throws Exception {
            mvc.perform(delete("/api/admin/products/not-an-id").with(admin()).with(csrf()))
                .andExpect(status().isBadRequest());
        }
    }
}
