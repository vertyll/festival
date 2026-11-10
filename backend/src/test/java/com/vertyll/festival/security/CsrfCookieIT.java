package com.vertyll.festival.security;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.vertyll.festival.MongoTestContainer;
import com.vertyll.festival.RedisTestContainer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The cookies as a browser receives them from the embedded server, with secure cookies on as in production. A browser
 * sends a {@code Secure} cookie only over HTTPS, so this test keeps the cookies itself and sends them over the plain
 * HTTP of the test server.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(
    {
        MongoTestContainer.class,
        RedisTestContainer.class
    }
)
class CsrfCookieIT {

    private static final String CSRF_COOKIE = "XSRF-TOKEN";
    private static final String CSRF_HEADER = "X-XSRF-TOKEN";
    private static final String SESSION_COOKIE = "__Host-FESTIVAL_SESSION";
    private static final String SET_COOKIE = "Set-Cookie";

    @LocalServerPort
    int port;

    private final Map<String, String> cookies = new LinkedHashMap<>();
    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    void meIssuesCsrfCookieThatUnlocksWrites() throws IOException, InterruptedException {
        assertThat(send(HttpRequest.newBuilder(uri("/api/me")).GET()).statusCode()).isEqualTo(200);
        String token = cookies.get(CSRF_COOKIE);
        assertThat(token).isNotBlank();

        assertThat(send(HttpRequest.newBuilder(uri("/logout")).POST(HttpRequest.BodyPublishers.noBody())).statusCode())
            .isEqualTo(403);
        assertThat(
            send(
                HttpRequest.newBuilder(uri("/logout"))
                    .header(CSRF_HEADER, token)
                    .POST(HttpRequest.BodyPublishers.noBody())
            ).statusCode()
        ).isEqualTo(200);
    }

    @Test
    void theCsrfCookieIsSecure() throws IOException, InterruptedException {
        HttpResponse<String> response = send(HttpRequest.newBuilder(uri("/api/me")).GET());

        assertThat(setCookie(response, CSRF_COOKIE)).contains("; Secure", "; SameSite=Lax").doesNotContain("HttpOnly");
    }

    @Test
    void theSessionCookieIsSecureAndBoundToThisHost() throws IOException, InterruptedException {
        HttpResponse<String> response = send(HttpRequest.newBuilder(uri("/oauth2/authorization/page")).GET());

        assertThat(setCookie(response, SESSION_COOKIE))
            .contains("; Path=/;", "; Secure", "; HttpOnly", "; SameSite=Lax")
            .doesNotContain("Domain=");
    }

    private HttpResponse<String> send(HttpRequest.Builder request) throws IOException, InterruptedException {
        if (!cookies.isEmpty()) {
            request.header(
                "Cookie",
                cookies.entrySet()
                    .stream()
                    .map(cookie -> cookie.getKey() + "=" + cookie.getValue())
                    .collect(Collectors.joining("; "))
            );
        }
        HttpResponse<String> response = client.send(request.build(), HttpResponse.BodyHandlers.ofString());
        for (String header : response.headers().allValues(SET_COOKIE)) {
            for (HttpCookie cookie : HttpCookie.parse(header)) {
                if (cookie.getMaxAge() == 0) {
                    cookies.remove(cookie.getName());
                } else {
                    cookies.put(cookie.getName(), cookie.getValue());
                }
            }
        }
        return response;
    }

    private static String setCookie(HttpResponse<String> response, String name) {
        List<String> named = response.headers()
            .allValues(SET_COOKIE)
            .stream()
            .filter(header -> header.startsWith(name + "="))
            .toList();
        assertThat(named).as("Set-Cookie for %s", name).hasSize(1);
        return named.getFirst();
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }
}
