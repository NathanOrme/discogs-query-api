package org.discogs.query.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Optimized cache configuration with different TTLs based on data volatility. This replaces the
 * previous one-size-fits-all cache configuration.
 */
@Slf4j
@Configuration
public class CacheConfig {

  /**
   * Creates optimized cache configurations with different TTLs for different data types.
   *
   * <p>Cache Strategy: - Search results: 5 minutes (volatile, user queries change frequently) -
   * Release details: 1 hour (stable, release info doesn't change often) - Marketplace data: 10
   * minutes (semi-volatile, prices change regularly) - Batch operations: 15 minutes (longer TTL for
   * expensive batch operations)
   *
   * @return a configured CacheManager with optimized cache settings
   */
  @Bean
  public CacheManager cacheManager() {
    SimpleCacheManager cacheManager = new SimpleCacheManager();

    // Search results cache - short TTL due to volatility
    Cache searchResultsCache =
        new CaffeineCache(
            "discogsResults",
            Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(500)
                .recordStats()
                .build());

    // String results cache - for raw API responses
    Cache stringResultsCache =
        new CaffeineCache(
            "stringResults",
            Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(300)
                .recordStats()
                .build());

    // Marketplace results cache - medium TTL
    Cache marketplaceCache =
        new CaffeineCache(
            "marketplaceResults",
            Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .recordStats()
                .build());

    // Collection releases cache - longer TTL as collection data is more stable
    Cache collectionCache =
        new CaffeineCache(
            "collectionReleases",
            Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(200)
                .recordStats()
                .build());

    // Batch marketplace check cache - longer TTL for expensive operations
    Cache batchMarketplaceCache =
        new CaffeineCache(
            "batchMarketplaceCheck",
            Caffeine.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(100)
                .recordStats()
                .build());

    cacheManager.setCaches(
        List.of(
            searchResultsCache,
            stringResultsCache,
            marketplaceCache,
            collectionCache,
            batchMarketplaceCache));

    log.info("Configured optimized cache manager with {} cache instances", 5);
    log.info("Cache configurations:");
    log.info("  - discogsResults: 5min TTL, 500 max entries");
    log.info("  - stringResults: 5min TTL, 300 max entries");
    log.info("  - marketplaceResults: 10min TTL, 1000 max entries");
    log.info("  - collectionReleases: 1hour TTL, 200 max entries");
    log.info("  - batchMarketplaceCheck: 15min TTL, 100 max entries");

    return cacheManager;
  }
}
