package org.discogs.query.limits;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.helpers.LogHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * An optimized rate limiter implementation that uses a queue-based approach instead of polling.
 * This restricts the number of operations to a specified limit per minute while allowing
 * non-blocking permit acquisition.
 */
@Slf4j
@Component
public class RateLimiter {

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final AtomicInteger requestCount = new AtomicInteger(0);
  private final LinkedBlockingQueue<CompletableFuture<Void>> waitingQueue = new LinkedBlockingQueue<>();

  @Value("${discogs.rate-limit}")
  int maxRequestsPerMinute;

  /**
   * Initializes the RateLimiter with the specified request limit per minute. The scheduler resets
   * the request count and processes queued requests every minute.
   */
  public RateLimiter() {
    LogHelper.debug(
        () -> "Initializing RateLimiter with a maximum of {} requests per minute.",
        maxRequestsPerMinute);
    scheduler.scheduleAtFixedRate(this::resetRequestCountAndProcessQueue, 1, 1, TimeUnit.MINUTES);
  }

  /** Resets the request count and processes any queued requests. */
  private void resetRequestCountAndProcessQueue() {
    requestCount.set(0);
    LogHelper.debug(() -> "Request count reset. Processing queued requests.");
    
    // Process queued requests up to the rate limit
    int processed = 0;
    while (processed < maxRequestsPerMinute && !waitingQueue.isEmpty()) {
      CompletableFuture<Void> queuedRequest = waitingQueue.poll();
      if (queuedRequest != null) {
        queuedRequest.complete(null);
        requestCount.incrementAndGet();
        processed++;
      }
    }
    LogHelper.debug(() -> "Processed {} queued requests.", processed);
  }

  /**
   * Attempts to acquire a permit for a request.
   *
   * @return true if a request can be made immediately, false if the rate limit has been reached
   */
  public boolean tryAcquire() {
    int currentCount = requestCount.incrementAndGet();
    if (currentCount > maxRequestsPerMinute) {
      requestCount.decrementAndGet(); // Roll back increment if limit exceeded
      LogHelper.debug(() -> "Rate limit exceeded. Current request count: {}", currentCount - 1);
      return false;
    }
    LogHelper.debug(() -> "Permit acquired. Current request count: {}", currentCount);
    return true;
  }

  /**
   * Acquires a permit for a request, queuing the request if the rate limit is exceeded.
   * This method returns a CompletableFuture that completes when a permit is available.
   *
   * @return CompletableFuture that completes when a permit is acquired
   */
  public CompletableFuture<Void> acquireAsync() {
    if (tryAcquire()) {
      return CompletableFuture.completedFuture(null);
    }
    
    CompletableFuture<Void> future = new CompletableFuture<>();
    waitingQueue.offer(future);
    LogHelper.debug(() -> "Request queued. Queue size: {}", waitingQueue.size());
    return future;
  }

  /** Shuts down the scheduler, releasing all resources. */
  public void shutdown() {
    log.info("Shutting down the RateLimiter scheduler...");
    
    // Complete any remaining queued requests with interruption
    waitingQueue.forEach(future -> future.completeExceptionally(new InterruptedException("Rate limiter shutdown")));
    waitingQueue.clear();
    
    scheduler.shutdown();
    try {
      if (!scheduler.awaitTermination(1, TimeUnit.MINUTES)) {
        LogHelper.warn(() -> "Forcing shutdown of the scheduler...");
        scheduler.shutdownNow();
      }
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      LogHelper.error(() -> "Shutdown interrupted. Forcing shutdown now.", e);
      scheduler.shutdownNow();
    }
    LogHelper.info(() -> "RateLimiter scheduler shutdown completed.");
  }
}