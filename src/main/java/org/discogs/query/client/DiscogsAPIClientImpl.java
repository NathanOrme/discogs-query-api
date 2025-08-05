package org.discogs.query.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.api.DiscogsCollectionRelease;
import org.discogs.query.domain.api.DiscogsMarketplaceResult;
import org.discogs.query.domain.api.DiscogsRelease;
import org.discogs.query.domain.api.DiscogsResult;
import org.discogs.query.exceptions.DiscogsMarketplaceException;
import org.discogs.query.exceptions.DiscogsSearchException;
import org.discogs.query.helpers.LogHelper;
import org.discogs.query.interfaces.DiscogsAPIClient;
import org.discogs.query.interfaces.HttpRequestService;
import org.discogs.query.interfaces.RateLimiterService;
import org.discogs.query.interfaces.RetryService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * A client component for interacting with the Discogs API.
 *
 * <p>This class uses {@link HttpRequestService} to send HTTP requests to the Discogs API, handles
 * responses, and manages retries and rate limits using {@link RetryService} and {@link
 * RateLimiterService}. It leverages Spring's caching abstraction with Caffeine to cache API
 * responses for improved performance.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiscogsAPIClientImpl implements DiscogsAPIClient {

  public static final String CACHE_MISS_FOR_SEARCH_URL = "Cache miss for " + "searchUrl: {}";

  private final HttpRequestService httpRequestService;
  private final RateLimiterService rateLimiterService;
  private final RetryService retryService;

  /**
   * Retrieves results from the Discogs API for a given search URL.
   *
   * <p>This method is cached using Spring's caching abstraction with Caffeine.
   *
   * @param searchUrl the URL to query the Discogs API
   * @return an instance of {@link DiscogsResult} containing the API response data
   * @throws DiscogsSearchException if an error occurs while fetching data from the Discogs API
   */
  @Cacheable(value = "discogsResults", key = "#searchUrl")
  @CircuitBreaker(name = "discogs-api", fallbackMethod = "fallbackGetResultsForQuery")
  @Retry(name = "discogs-api")
  @Override
  public DiscogsResult getResultsForQuery(final String searchUrl) {
    LogHelper.info(() -> CACHE_MISS_FOR_SEARCH_URL, searchUrl);
    return executeWithRateLimitAndRetry(
        () -> httpRequestService.executeRequest(searchUrl, DiscogsResult.class),
        "Discogs Search API Request");
  }

  /**
   * Retrieves a string result from the Discogs API for a given search URL.
   *
   * <p>This method is cached using Spring's caching abstraction with Caffeine.
   *
   * @param searchUrl the URL to query the Discogs API
   * @return a {@link String} containing the API response data
   * @throws DiscogsSearchException if an error occurs while fetching data from the Discogs API
   */
  @Cacheable(value = "stringResults", key = "#searchUrl")
  @Override
  public String getStringResultForQuery(final String searchUrl) {
    LogHelper.info(() -> CACHE_MISS_FOR_SEARCH_URL, searchUrl);
    return executeWithRateLimitAndRetry(
        () -> httpRequestService.executeRequest(searchUrl, String.class),
        "Discogs Search API Request");
  }

  /**
   * Checks whether the given item is listed on the Discogs Marketplace.
   *
   * <p>This method is cached using Spring's caching abstraction with Caffeine.
   *
   * @param url the URL pointing to the item on the Discogs Marketplace
   * @return a {@link DiscogsMarketplaceResult} object containing the details of the item on the
   *     marketplace
   * @throws DiscogsSearchException if an error occurs while fetching data from the Discogs API
   */
  @Cacheable(value = "marketplaceResults", key = "#url")
  @CircuitBreaker(name = "discogs-api", fallbackMethod = "fallbackGetMarketplaceResultForQuery")
  @Retry(name = "discogs-api")
  @Override
  public DiscogsMarketplaceResult getMarketplaceResultForQuery(final String url) {
    LogHelper.info(() -> "Cache miss for url: {}", url);
    return executeWithRateLimitAndRetry(
        () -> httpRequestService.executeRequest(url, DiscogsMarketplaceResult.class),
        "Discogs Marketplace API Request");
  }

  /**
   * Checks whether the given item is listed on the Discogs Marketplace.
   *
   * <p>This method is cached using Spring's caching abstraction with Caffeine.
   *
   * @param url the URL pointing to the item on the Discogs Marketplace
   * @return a {@link DiscogsCollectionRelease} object containing the details of the item on the
   *     marketplace
   * @throws DiscogsSearchException if an error occurs while fetching data from the Discogs API
   */
  @Cacheable(value = "collectionReleases", key = "#url")
  @Override
  public DiscogsCollectionRelease getCollectionReleases(final String url) {
    LogHelper.info(() -> "Cache miss for url: {}", url);
    return executeWithRateLimitAndRetry(
        () -> httpRequestService.executeRequest(url, DiscogsCollectionRelease.class),
        "Discogs Collections Release API Request");
  }

  /**
   * Checks whether the given item is listed on the Discogs Marketplace.
   *
   * <p>This method is cached using Spring's caching abstraction with Caffeine.
   *
   * @param url the URL pointing to the item on the Discogs Marketplace
   * @return a {@link DiscogsRelease} object containing the details of the item on the marketplace
   * @throws DiscogsSearchException if an error occurs while fetching data from the Discogs API
   */
  @Cacheable(value = "marketplaceResults", key = "#url")
  @Override
  public DiscogsRelease getRelease(final String url) {
    LogHelper.info(() -> "Cache miss for url: {}", url);
    return executeWithRateLimitAndRetry(
        () -> httpRequestService.executeRequest(url, DiscogsRelease.class),
        "Discogs Release API Request");
  }

  /**
   * Executes a callable action with rate limit and retry logic.
   *
   * <p>This method ensures the rate limit is respected before executing the action and retries the
   * action in case of failure.
   *
   * @param action the callable action to be executed
   * @param actionDescription a description of the action being performed
   * @param <T> the type of the result returned by the action
   * @return the result of the action
   * @throws DiscogsSearchException if an error occurs while fetching data from the Discogs API
   *     after all retry attempts
   * @throws DiscogsMarketplaceException if an error occurs while fetching data from the Discogs
   *     Marketplace API after all retry attempts
   */
  private <T> T executeWithRateLimitAndRetry(
      final Callable<T> action, final String actionDescription) {
    try {
      rateLimiterService.waitForRateLimit(); // Ensure rate limit
      // before executing the action
      return retryService.executeWithRetry(action, actionDescription);
    } catch (final Exception e) {
      if (DiscogsMarketplaceResult.class.isAssignableFrom(action.getClass())) {
        throw new DiscogsMarketplaceException(
            "Failed to fetch data from Discogs Marketplace API", e);
      }
      throw new DiscogsSearchException("Failed to fetch data from Discogs API", e);
    }
  }

  /**
   * Fallback method for getResultsForQuery when circuit breaker is open.
   *
   * @param searchUrl the URL that was being queried
   * @param ex the exception that triggered the fallback
   * @return an empty DiscogsResult to gracefully handle failures
   */
  public DiscogsResult fallbackGetResultsForQuery(final String searchUrl, final Exception ex) {
    LogHelper.warn(
        () -> "Circuit breaker fallback triggered for search URL: {}. Error: {}",
        searchUrl,
        ex.getMessage());
    return new DiscogsResult(); // Return empty result instead of failing
  }

  /**
   * Fallback method for getMarketplaceResultForQuery when circuit breaker is open.
   *
   * @param url the URL that was being queried
   * @param ex the exception that triggered the fallback
   * @return an empty DiscogsMarketplaceResult to gracefully handle failures
   */
  public DiscogsMarketplaceResult fallbackGetMarketplaceResultForQuery(
      final String url, final Exception ex) {
    LogHelper.warn(
        () -> "Circuit breaker fallback triggered for marketplace URL: {}. Error: {}",
        url,
        ex.getMessage());
    return new DiscogsMarketplaceResult(); // Return empty result instead of failing
  }
}
