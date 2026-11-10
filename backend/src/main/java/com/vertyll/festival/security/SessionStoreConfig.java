package com.vertyll.festival.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson.SecurityJacksonModules;
import org.springframework.security.web.savedrequest.SimpleSavedRequest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.module.SimpleModule;

/**
 * How the attributes of a session are written to Redis: as JSON, never as Java serialization.
 *
 * <p>
 * A session holds the security context, the {@code OAuth2AuthorizedClient} with the person's tokens and the page a
 * sign-in returns to. Java serialization would tie each stored session to the exact classes of the release that wrote
 * it, and reading it back would instantiate whatever classes its bytes name. Spring Security's Jackson modules map its
 * own types and allow only those, and the collections they hold, as polymorphic types, so a stored value naming any
 * other class is refused. Spring Session picks the serializer up by its bean name.
 */
@Configuration(proxyBeanMethods = false)
class SessionStoreConfig {

    /**
     * The collections {@code List.of}, {@code Set.of} and {@code Map.of} return, which the claims of a sign-in's ID
     * token can be held in; Spring Security's own list allows only the collections it creates itself.
     */
    private static final String JDK_IMMUTABLE_COLLECTIONS = "java.util.ImmutableCollections$";

    @Bean
    RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return sessionSerializer();
    }

    static RedisSerializer<Object> sessionSerializer() {
        SimpleModule returnPage = new SimpleModule("ReturnPage");
        returnPage.setMixInAnnotation(SimpleSavedRequest.class, SimpleSavedRequestMixin.class);
        return new GenericJacksonJsonRedisSerializer(
            JsonMapper.builder()
                .addModules(
                    SecurityJacksonModules.getModules(
                        Thread.currentThread().getContextClassLoader(),
                        BasicPolymorphicTypeValidator.builder()
                            .allowIfSubType(JDK_IMMUTABLE_COLLECTIONS)
                            .allowIfSubType(SimpleSavedRequest.class)
                    )
                )
                .addModule(returnPage)
                .build()
        );
    }

    /**
     * The saved request {@link ReturnPage} keeps, which Spring Security's modules do not map: only its address is
     * written, the one thing {@code ReturnPage} sets.
     */
    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
    )
    @JsonIgnoreProperties(ignoreUnknown = true)
    abstract static class SimpleSavedRequestMixin {

        @JsonCreator
        SimpleSavedRequestMixin(@JsonProperty("redirectUrl") String redirectUrl) {
        }

        @JsonProperty("redirectUrl")
        abstract String getRedirectUrl();
    }
}
