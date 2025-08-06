package org.discogs.query.service.requests;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.helpers.LogHelper;
import org.discogs.query.interfaces.RateLimiterService;
import org.discogs.query.limits.RateLimiter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimiterServiceImpl implements RateLimiterService {

  private final RateLimiter rateLimiter;

  @Override
  public void waitForRateLimit() {
    LogHelper.debug(() -> "Starting to check rate limiter status...");

    while (!rateLimiter.tryAcquire()) {
      try {
        LogHelper.info(() -> "Rate limit reached. Waiting to acquire permit...");
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
        LogHelper.error(() -> "Thread interrupted while waiting for rate limit to reset", e);
        return;
      }
    }
    LogHelper.debug(() -> "Acquired permit from rate limiter, proceeding with execution.");
  }

  @Override
  public CompletableFuture<Void> acquireRateLimitAsync() {
    LogHelper.debug(() -> "Attempting to acquire rate limit asynchronously...");
    return rateLimiter.acquireAsync()
        .whenComplete((result, throwable) -> {
          if (throwable == null) {
            LogHelper.debug(() -> "Async rate limit permit acquired successfully.");
          } else {
            LogHelper.error(() -> "Failed to acquire async rate limit permit", throwable);
          }
        });
  }
}
