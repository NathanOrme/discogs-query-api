package org.discogs.query.config;

import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CacheConfigTest {

    @Test
    void testCacheManagerConfiguration() {
        CacheConfig cacheConfig = new CacheConfig();
        CacheManager cacheManager = cacheConfig.cacheManager();

        assertInstanceOf(CaffeineCacheManager.class, cacheManager);
        CaffeineCacheManager caffeineCacheManager = (CaffeineCacheManager) cacheManager;
        assertNotNull(caffeineCacheManager.getCache("default")); // Ensure default cache is configured
    }
}
