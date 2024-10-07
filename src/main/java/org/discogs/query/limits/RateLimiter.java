package org.discogs.query.limits;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple rate limiter implementation that restricts the number of operations
 * to a specified limit per minute.
 */
@Slf4j
@Component
public class RateLimiter {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final AtomicInteger requestCount = new AtomicInteger(0);

    @Value("${discogs.rate-limit}")
    int maxRequestsPerMinute;

    /**
     * Initializes the RateLimiter with the specified request limit per minute.
     * The scheduler resets the request count every minute.
     */
    public RateLimiter() {
        if (log.isDebugEnabled()) {
            log.debug("Initializing RateLimiter with a maximum of {} requests per minute.", maxRequestsPerMinute);
        }
        scheduler.scheduleAtFixedRate(this::resetRequestCount, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * Resets the request count to zero at the start of each minute.
     */
    private void resetRequestCount() {
        requestCount.set(0);
        if (log.isDebugEnabled()) {
            log.debug("Request count reset. Ready for new requests.");
        }
    }

    /**
     * Attempts to acquire a permit for a request.
     *
     * @return true if a request can be made, false if the rate limit has been reached
     */
    public boolean tryAcquire() {
        int currentCount = requestCount.incrementAndGet();
        if (currentCount > maxRequestsPerMinute) {
            requestCount.decrementAndGet(); // Roll back increment if limit exceeded
            if (log.isDebugEnabled()) {
                log.debug("Rate limit exceeded. Current request count: {}", currentCount - 1);
            }
            return false;
        }
        if (log.isDebugEnabled()) {
            log.debug("Permit acquired. Current request count: {}", currentCount);
        }
        return true;
    }

    /**
     * Shuts down the scheduler, releasing all resources.
     */
    public void shutdown() {
        log.info("Shutting down the RateLimiter scheduler...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.MINUTES)) {
                if (log.isWarnEnabled()) {
                    log.warn("Forcing shutdown of the scheduler...");
                }
                scheduler.shutdownNow();
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            if (log.isErrorEnabled()) {
                log.error("Shutdown interrupted. Forcing shutdown now.", e);
            }
            scheduler.shutdownNow();
        }
        log.info("RateLimiter scheduler shutdown completed.");
    }
}
