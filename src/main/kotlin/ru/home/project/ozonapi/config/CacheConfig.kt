package ru.home.project.ozonapi.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import java.time.Duration

/**
 * @author rlagay
 */
@Configuration
class CacheConfig {

    @Bean
    fun cacheConfiguration() : RedisCacheConfiguration {
        val objectMapper = ObjectMapper()
        objectMapper.registerModules(JavaTimeModule())
        objectMapper.registerKotlinModule()
        return RedisCacheConfiguration.defaultCacheConfig()
            .disableCachingNullValues()
            .entryTtl(Duration.ofMinutes(10))
            .prefixCacheNameWith("ozon")
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                GenericJackson2JsonRedisSerializer().configure {
                    it.registerModules(JavaTimeModule())
                    it.registerKotlinModule()
                }
            ))
    }
}