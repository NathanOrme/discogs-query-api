package org.discogs.query.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration class for setting up caching in the application.
 * <p>
 * This class is annotated with {@link Configuration},
 * indicating that it provides Spring configuration.
 * It defines a {@link CacheManager} bean that uses Caffeine for caching.
 * </p>
 */
@Configuration
public class CacheConfig {

    /**
     * Creates and configures a {@link CacheManager} bean using Caffeine.
     * <p>
     * This method sets up a {@link CaffeineCacheManager} with specific cache settings:
     * <ul>
     *     <li>Cache entries will expire 10 minutes after they are written.</li>
     *     <li>The cache will have a maximum size of 1000 entries.</li>
     * </ul>
     * </p>
     *
     * @return a configured {@link CacheManager} instance
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES) // Configure cache expiration
                .maximumSize(1000)); // Configure maximum cache size
        return cacheManager;
    }
}
