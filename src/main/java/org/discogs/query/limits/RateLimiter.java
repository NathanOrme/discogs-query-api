package org.discogs.query.limits;

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
@Component
public class RateLimiter {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final AtomicInteger requestCount = new AtomicInteger(0);

    @Value("${discogs.rate-limit}")
    private int maxRequestsPerMinute;

    /**
     * Creates a new RateLimiter with the given limit on requests per minute.
     */
    public RateLimiter() {
        scheduler.scheduleAtFixedRate(this::resetRequestCount, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * Resets the request count to zero at the start of each minute.
     */
    private void resetRequestCount() {
        requestCount.set(0);
    }

    /**
     * Attempts to acquire a permit for a request.
     *
     * @return true if a request can be made, false if the rate limit has been reached
     */
    public boolean tryAcquire() {
        return requestCount.incrementAndGet() <= maxRequestsPerMinute;
    }

    /**
     * Shuts down the scheduler, releasing all resources.
     */
    public void shutdown() {
        scheduler.shutdown();
    }
}
