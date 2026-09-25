package com.vertyll.festival.config;

import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.deser.std.StdScalarDeserializer;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Configuration(proxyBeanMethods = false)
class JacksonConfig {

    @Bean
    JacksonModule objectIdModule() {
        return new SimpleModule("festival-object-id").addSerializer(ObjectId.class, ToStringSerializer.instance)
            .addDeserializer(ObjectId.class, new ObjectIdDeserializer());
    }

    static final class ObjectIdDeserializer extends StdScalarDeserializer<ObjectId> {

        ObjectIdDeserializer() {
            super(ObjectId.class);
        }

        @Override
        public ObjectId deserialize(JsonParser parser, DeserializationContext context) {
            String value = parser.getValueAsString();
            if (value == null || !ObjectId.isValid(value)) {
                throw context.weirdStringException(String.valueOf(value), ObjectId.class, "invalid identifier");
            }
            return new ObjectId(value);
        }
    }
}
