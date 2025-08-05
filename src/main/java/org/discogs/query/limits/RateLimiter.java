package org.discogs.query.limits;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.helpers.LogHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Token bucket rate limiter implementation that provides smooth rate limiting with burst capacity.
 * This replaces the previous busy-wait approach with an efficient token bucket algorithm.
 */
@Slf4j
@Component
public class RateLimiter {

  private final AtomicLong tokens;
  private final AtomicReference<Instant> lastRefill = new AtomicReference<>(Instant.now());
  private final long maxTokens;
  private final long refillRate; // tokens per second
  private final long burstCapacity;

  @Value("${discogs.rate-limit:60}")
  int maxRequestsPerMinute;

  @Value("${discogs.rate-limit-burst:10}")
  int burstSize;

  /** Initializes the TokenBucket RateLimiter with the specified parameters. */
  public RateLimiter() {
    // Convert requests per minute to requests per second for smoother distribution
    this.refillRate = Math.max(1, maxRequestsPerMinute / 60);
    this.burstCapacity = Math.max(burstSize, refillRate * 2); // Allow some burst
    this.maxTokens = burstCapacity;
    this.tokens = new AtomicLong(maxTokens);

    LogHelper.info(
        () ->
            "TokenBucket RateLimiter initialized: {} requests/minute, {} burst capacity, {} refill"
                + " rate/sec",
        maxRequestsPerMinute,
        burstCapacity,
        refillRate);
  }

  /**
   * Attempts to acquire a permit for a request using token bucket algorithm. This method is
   * non-blocking and does not use busy-waiting.
   *
   * @return true if a token was acquired, false if rate limit is exceeded
   */
  public boolean tryAcquire() {
    return tryAcquire(1);
  }

  /**
   * Attempts to acquire the specified number of tokens.
   *
   * @param tokensRequested number of tokens to acquire
   * @return true if tokens were acquired, false if insufficient tokens available
   */
  public boolean tryAcquire(final long tokensRequested) {
    if (tokensRequested <= 0) {
      return true;
    }

    refillTokens();

    long currentTokens = tokens.get();
    while (currentTokens >= tokensRequested) {
      if (tokens.compareAndSet(currentTokens, currentTokens - tokensRequested)) {
        LogHelper.debug(
            () -> "Acquired {} tokens. Remaining: {}",
            tokensRequested,
            currentTokens - tokensRequested);
        return true;
      }
      currentTokens = tokens.get(); // Retry with updated value
    }

    LogHelper.debug(
        () -> "Rate limit exceeded. Requested: {}, Available: {}", tokensRequested, currentTokens);
    return false;
  }

  /**
   * Refills the token bucket based on elapsed time since last refill. Uses atomic operations to
   * ensure thread safety.
   */
  private void refillTokens() {
    Instant now = Instant.now();
    Instant lastRefillTime = lastRefill.get();

    if (lastRefill.compareAndSet(lastRefillTime, now)) {
      long elapsedSeconds = Duration.between(lastRefillTime, now).getSeconds();
      if (elapsedSeconds > 0) {
        long tokensToAdd = Math.min(elapsedSeconds * refillRate, maxTokens);
        if (tokensToAdd > 0) {
          long currentTokens;
          long newTokens;
          do {
            currentTokens = tokens.get();
            newTokens = Math.min(currentTokens + tokensToAdd, maxTokens);
          } while (!tokens.compareAndSet(currentTokens, newTokens));

          if (newTokens > currentTokens) {
            LogHelper.debug(
                () -> "Refilled {} tokens after {} seconds. Total: {}",
                newTokens - currentTokens,
                elapsedSeconds,
                newTokens);
          }
        }
      }
    }
  }

  /**
   * Returns the current number of available tokens. Useful for monitoring and debugging.
   *
   * @return current token count
   */
  public long getAvailableTokens() {
    refillTokens();
    return tokens.get();
  }

  /**
   * Returns the maximum token capacity of this rate limiter.
   *
   * @return maximum token capacity
   */
  public long getMaxTokens() {
    return maxTokens;
  }

  /**
   * No longer needs explicit shutdown as this implementation doesn't use threads. Kept for backward
   * compatibility.
   */
  public void shutdown() {
    LogHelper.info(() -> "TokenBucket RateLimiter shutdown completed (no resources to cleanup).");
  }
}
