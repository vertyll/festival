package com.vertyll.festival.security;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.vertyll.festival.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

/** The CSRF cookie with secure cookies off, as the {@code local} profile serves it over plain HTTP. */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "server.servlet.session.cookie.secure=false",
        "server.servlet.session.cookie.name=FESTIVAL_SESSION"
    }
)
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class LocalCsrfCookieIT {

    @LocalServerPort
    int port;

    @Test
    void theCsrfCookieDropsThePrefixAndSecure() throws IOException, InterruptedException {
        List<String> cookies;
        try (HttpClient http = HttpClient.newHttpClient()) {
            HttpResponse<Void> response = http.send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/me")).GET().build(),
                HttpResponse.BodyHandlers.discarding()
            );
            cookies = response.headers().allValues("Set-Cookie");
        }

        assertThat(cookies).noneMatch(header -> header.startsWith("__Host-XSRF-TOKEN="));
        assertThat(cookies).filteredOn(header -> header.startsWith("XSRF-TOKEN="))
            .singleElement()
            .asString()
            .contains("; Path=/;", "; SameSite=Lax")
            .doesNotContain("Secure", "Domain=", "HttpOnly");
    }
}
