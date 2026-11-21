package com.vertyll.festival;

import java.time.Duration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.mongodb.MongoDBContainer;

// Spring stops the containers when the test context closes.
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    MongoDBContainer mongoDbContainer() {
        return new MongoDBContainer("mongo:9").withStartupTimeout(Duration.ofMinutes(2)).withStartupAttempts(3);
    }

    @Bean
    @ServiceConnection(name = "redis")
    GenericContainer<?> redisContainer() {
        return new GenericContainer<>("redis:8-alpine").withStartupTimeout(Duration.ofMinutes(2))
            .withStartupAttempts(3)
            .withExposedPorts(6379);
    }
}
