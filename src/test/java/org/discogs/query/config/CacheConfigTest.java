package org.discogs.query.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringJUnitConfig(classes = CacheConfig.class)
public class CacheConfigTest {

    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        // Using a shorter expiration time (1 second) for testing purposes
        cacheManager = new CacheConfig.CustomConcurrentMapCacheManager(1, "discogsResults", "discogsMarketplaceResults");
    }

    @Test
    void cacheManager_ShouldInitializeWithConfiguredCaches() {
        assertNotNull(cacheManager);

        Cache discogsResultsCache = cacheManager.getCache("discogsResults");
        Cache discogsMarketplaceResultsCache = cacheManager.getCache("discogsMarketplaceResults");

        assertNotNull(discogsResultsCache);
        assertNotNull(discogsMarketplaceResultsCache);
    }

    @Test
    void cache_ShouldStoreAndRetrieveValue() {
        Cache cache = cacheManager.getCache("discogsResults");
        assertNotNull(cache);

        String key = "testKey";
        String value = "testValue";

        cache.put(key, value);

        Cache.ValueWrapper retrievedValue = cache.get(key);
        assertNotNull(retrievedValue);
        assertEquals(value, retrievedValue.get());
    }

    @Test
    void cache_ShouldEvictExpiredEntries() throws InterruptedException {
        Cache cache = cacheManager.getCache("discogsResults");
        assertNotNull(cache);

        String key = "testKey";
        String value = "testValue";

        cache.put(key, value);

        // Ensure the value is present initially
        Cache.ValueWrapper retrievedValue = cache.get(key);
        assertNotNull(retrievedValue);
        assertEquals(value, retrievedValue.get());

        // Wait for expiration (assuming the expiration time is set to 1 second for this test)
        TimeUnit.SECONDS.sleep(2);

        // Now, the entry should be evicted due to expiration
        Cache.ValueWrapper expiredValue = cache.get(key);
        assertNull(expiredValue);
    }

    @Test
    void cache_ShouldEvictEntryAfterManualEviction() {
        Cache cache = cacheManager.getCache("discogsResults");
        assertNotNull(cache);

        String key = "testKey";
        String value = "testValue";

        cache.put(key, value);

        // Ensure the value is present initially
        Cache.ValueWrapper retrievedValue = cache.get(key);
        assertNotNull(retrievedValue);
        assertEquals(value, retrievedValue.get());

        // Evict the entry manually
        cache.evict(key);

        // Now, the entry should be evicted
        Cache.ValueWrapper evictedValue = cache.get(key);
        assertNull(evictedValue);
    }

    @Test
    void cache_ShouldAllowNullValuesIfConfigured() {
        Cache cache = cacheManager.getCache("discogsResults");
        assertNotNull(cache);

        String key = "testKey";

        // Put null value into the cache
        cache.put(key, null);

        // Ensure the null value is stored and retrieved correctly
        Cache.ValueWrapper retrievedValue = cache.get(key);
        assertNotNull(retrievedValue);  // ValueWrapper should not be null
        assertNull(retrievedValue.get());  // The actual value should be null
    }

    @Test
    void customCacheManager_ShouldUseCorrectExpirationDuration() {
        // Using a shorter expiration time (1 second) for testing purposes
        CacheConfig.CustomConcurrentMapCacheManager cacheManager = new CacheConfig.CustomConcurrentMapCacheManager(1, "testCache");

        Cache cache = cacheManager.getCache("testCache");
        assertNotNull(cache);

        String key = "testKey";
        String value = "testValue";

        cache.put(key, value);

        Cache.ValueWrapper retrievedValue = cache.get(key);
        assertNotNull(retrievedValue);
        assertEquals(value, retrievedValue.get());

        // Wait for expiration (set for 1 second in the constructor)
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Check that the value has expired
        Cache.ValueWrapper expiredValue = cache.get(key);
        assertNull(expiredValue);
    }
}
