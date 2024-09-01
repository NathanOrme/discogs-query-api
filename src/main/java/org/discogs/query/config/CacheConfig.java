package org.discogs.query.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Configuration class for setting up caching in the application.
 * <p>
 * This configuration enables caching and defines a custom in-memory cache with expiration policy.
 * The cache is managed by {@link CustomConcurrentMapCacheManager} which creates caches with expiration capabilities.
 * The expiration time can be configured via application properties.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * The duration (in seconds) after which cache entries expire.
     * <p>
     * The value is configurable via the `cache.expiration.duration` property.
     * The default value is set to 600 seconds (10 minutes).
     */
    @Value("${cache.expiration.duration:600}")
    private long cacheExpirationDuration;

    /**
     * Bean definition for {@link CacheManager}.
     * <p>
     * This method returns a {@link CustomConcurrentMapCacheManager} instance configured with specific cache names.
     * The caches will have entries that expire after the configured duration.
     *
     * @return a configured {@link CacheManager} instance with custom expiration policy
     */
    @Bean
    public CacheManager cacheManager() {
        return new CustomConcurrentMapCacheManager(
                cacheExpirationDuration, "discogsResults", "discogsMarketplaceResults");
    }

    /**
     * Custom implementation of {@link ConcurrentMapCacheManager} that creates caches with expiration capabilities.
     * <p>
     * This class overrides the {@link ConcurrentMapCacheManager#createConcurrentMapCache(String)} method to
     * provide cache instances that support expiration of entries.
     */
    static class CustomConcurrentMapCacheManager extends ConcurrentMapCacheManager {

        private final long cacheExpirationDuration;

        /**
         * Constructs a {@link CustomConcurrentMapCacheManager} with specified cache names and expiration duration.
         * <p>
         * This constructor initializes the cache manager with the provided cache names, enabling caching
         * for those caches with expiration policy.
         *
         * @param cacheExpirationDuration the duration (in seconds) after which cache entries expire
         * @param cacheNames              the names of the caches to be managed
         */
        public CustomConcurrentMapCacheManager(final long cacheExpirationDuration, final String... cacheNames) {
            super(cacheNames);
            this.cacheExpirationDuration = cacheExpirationDuration;
        }

        /**
         * Creates a cache instance with expiration capabilities.
         * <p>
         * This method overrides the default cache creation behavior to provide a custom cache that
         * supports entry expiration based on the configured expiration duration.
         *
         * @param name the name of the cache
         * @return a {@link ConcurrentMapCache} instance with expiration support
         */
        @Override
        protected ConcurrentMapCache createConcurrentMapCache(final String name) {
            return new ExpiringConcurrentMapCache(name, new ConcurrentHashMap<>(),
                    true, cacheExpirationDuration, TimeUnit.SECONDS);
        }
    }

    /**
     * Custom implementation of {@link ConcurrentMapCache} that supports expiration of cache entries.
     * <p>
     * This class overrides the {@link ConcurrentMapCache#get(Object)}
     * and {@link ConcurrentMapCache#put(Object, Object)}
     * methods to provide expiration functionality for cache entries.
     */
    static class ExpiringConcurrentMapCache extends ConcurrentMapCache {

        private final long expirationTimeMillis;

        /**
         * Constructs an {@link ExpiringConcurrentMapCache} with the given parameters.
         * <p>
         * The cache entries will expire after the specified duration, which is provided in the form of
         * a time unit and duration.
         *
         * @param name            the name of the cache
         * @param store           the underlying store for cache entries
         * @param allowNullValues whether to allow null values in the cache
         * @param duration        the expiration duration
         * @param unit            the time unit for the expiration duration
         */
        public ExpiringConcurrentMapCache(final String name, final ConcurrentHashMap<Object, Object> store,
                                          final boolean allowNullValues, final long duration, final TimeUnit unit) {
            super(name, store, allowNullValues);
            this.expirationTimeMillis = unit.toMillis(duration);
        }

        /**
         * Retrieves a value from the cache, considering expiration.
         * <p>
         * If the cache entry is expired, it is evicted from the cache and {@code null} is returned.
         *
         * @param key the key whose associated value is to be returned
         * @return the value associated with the key, or {@code null} if the entry is expired or not present
         */
        @Override
        public ValueWrapper get(final Object key) {
            ExpiringValueWrapper valueWrapper = (ExpiringValueWrapper) super.get(key);
            if (valueWrapper == null || valueWrapper.isExpired()) {
                evict(key);  // Remove expired entry from cache
                return null;
            }
            return valueWrapper;
        }

        /**
         * Adds a value to the cache with an expiration policy.
         * <p>
         * The value is wrapped in an {@link ExpiringValueWrapper} that keeps track of the expiration time.
         *
         * @param key   the key with which the specified value is to be associated
         * @param value the value to be associated with the specified key
         */
        @Override
        public void put(final Object key, final Object value) {
            super.put(key, new ExpiringValueWrapper(value, expirationTimeMillis));
        }

        /**
         * Wrapper for cache values that supports expiration.
         * <p>
         * This class wraps the actual cache value and stores the expiration time.
         * It provides a method to check if the value is expired.
         */
        static class ExpiringValueWrapper implements ValueWrapper {
            private final Object value;
            private final long expirationTime;

            /**
             * Constructs an {@link ExpiringValueWrapper} with the given value and expiration duration.
             * <p>
             * The expiration time is calculated based on the current time and the provided duration.
             *
             * @param value              the cached value
             * @param expirationDuration the duration until the cache entry expires
             */
            ExpiringValueWrapper(final Object value, final long expirationDuration) {
                this.value = value;
                this.expirationTime = System.currentTimeMillis() + expirationDuration;
            }

            /**
             * Checks if the value has expired.
             * <p>
             * This method compares the current time with the expiration time to determine if the entry is expired.
             *
             * @return {@code true} if the value is expired, {@code false} otherwise
             */
            boolean isExpired() {
                return System.currentTimeMillis() > expirationTime;
            }

            /**
             * Returns the actual cached value.
             *
             * @return the cached value
             */
            @Override
            public Object get() {
                return value;
            }
        }
    }
}
