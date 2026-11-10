package com.vertyll.festival.keycloak;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;

/**
 * How the application reaches Keycloak over HTTP: the account API, the token endpoint and the key set alike, through
 * one client whose every connection and every reply is bounded by ten seconds, so a Keycloak that stops answering fails
 * a request instead of holding its thread.
 */
@Component
public final class KeycloakHttp implements AutoCloseable {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient client = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();

    /** A request factory on the shared client, which also waits no longer than the timeout for a reply. */
    public JdkClientHttpRequestFactory requestFactory() {
        JdkClientHttpRequestFactory requests = new JdkClientHttpRequestFactory(client);
        requests.setReadTimeout(TIMEOUT);
        return requests;
    }

    @Override
    public void close() {
        client.close();
    }
}
