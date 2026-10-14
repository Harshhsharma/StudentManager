
package com.example.springdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {

        GenericJacksonJsonRedisSerializer serializer =
                GenericJacksonJsonRedisSerializer.builder()
                        .enableDefaultTyping(
                                tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator
                                        .builder()
                                        .allowIfBaseType(Object.class)
                                        .allowIfSubType(
                                                "com.example.springdemo.Dto"
                                        )
                                        .build()
                        )
                        .build();

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ZERO)
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer)
                );
    }
}
