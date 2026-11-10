package com.vertyll.festival.config;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions.BigDecimalRepresentation;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import com.mongodb.WriteConcern;

@Configuration(proxyBeanMethods = false)
class MongoConfig {

    /**
     * A write is acknowledged once a majority of the replica set holds it, so a primary that fails over does not take
     * an acknowledged order with it; connecting, choosing a server and reading a reply are each bounded, so an
     * unreachable database fails a request instead of holding its thread.
     */
    @Bean
    MongoClientSettingsBuilderCustomizer mongoClientDurabilityAndTimeouts(MongoClientProperties properties) {
        return settings -> settings.writeConcern(WriteConcern.MAJORITY)
            .applyToClusterSettings(
                cluster -> cluster
                    .serverSelectionTimeout(properties.serverSelectionTimeout().toMillis(), TimeUnit.MILLISECONDS)
            )
            .applyToSocketSettings(
                socket -> socket.connectTimeout(properties.connectTimeout().toMillis(), TimeUnit.MILLISECONDS)
                    .readTimeout(properties.readTimeout().toMillis(), TimeUnit.MILLISECONDS)
            );
    }

    @Bean
    MongoCustomConversions mongoCustomConversions() {
        return MongoCustomConversions
            .create(adapter -> adapter.useNativeDriverJavaTimeCodecs().bigDecimal(BigDecimalRepresentation.DECIMAL128));
    }

    @Bean
    MappingMongoConverter mappingMongoConverter(
        MongoDatabaseFactory databaseFactory,
        MongoMappingContext mappingContext,
        MongoCustomConversions conversions
    ) {
        MappingMongoConverter converter =
                new MappingMongoConverter(new DefaultDbRefResolver(databaseFactory), mappingContext);
        converter.setCustomConversions(conversions);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }
}
