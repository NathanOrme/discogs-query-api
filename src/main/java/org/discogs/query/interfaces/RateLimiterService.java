package org.discogs.query.interfaces;

import java.util.concurrent.CompletableFuture;

/**
 * Service interface for managing rate limits.
 *
 * <p>This interface defines methods for ensuring that requests adhere to the rate limits.
 */
public interface RateLimiterService {

  /**
   * Waits for the rate limiter to allow a request to proceed. This method blocks until the rate
   * limiter permits a request.
   */
  void waitForRateLimit();

  /**
   * Asynchronously acquires a rate limit permit. This method returns a CompletableFuture that
   * completes when a permit is available, allowing for non-blocking rate limit handling.
   *
   * @return CompletableFuture that completes when a rate limit permit is acquired
   */
  CompletableFuture<Void> acquireRateLimitAsync();
}
