package org.discogs.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.interfaces.RateLimiterService;
import org.discogs.query.limits.RateLimiter;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Implementation of the {@link RateLimiterService} interface that manages
 * rate limiting using a {@link RateLimiter}.
 * This class is responsible for ensuring that requests do not exceed the
 * predefined rate limits.
 * It uses a rate limiter to control the rate of requests and waits until a
 * permit is available if the rate limit is reached.
 *
 * <p>It logs debug and info messages to help track the status of rate
 * limiting and any delays encountered.
 *
 * <p>This class is annotated with {@link Component} to be recognized by
 * Spring's component scanning and with
 * {@link RequiredArgsConstructor} to generate a constructor for the final
 * fields.
 *
 * <p>Example usage:
 * <pre>
 * {@code
 * RateLimiterService rateLimiterService = new RateLimiterServiceImpl(rateLimiter);
 * rateLimiterService.waitForRateLimit();
 * }
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimiterServiceImpl implements RateLimiterService {

    private final RateLimiter rateLimiter;

    /**
     * Waits for the rate limiter to permit the current operation to proceed.
     *
     * <p>This method will continually check the rate limiter until a permit
     * is available.
     * If the rate limit is reached, it will log an informational message and
     * sleep for a short period (100 milliseconds)
     * before trying again. If the thread is interrupted during this sleep
     * period, an error message is logged and
     * the method will return early.
     *
     * <p>Upon acquiring a permit, a debug message is logged, and the method
     * will proceed with execution.
     */
    @Override
    public void waitForRateLimit() {
        log.debug("Starting to check rate limiter status...");

        while (!rateLimiter.tryAcquire()) {
            try {
                log.info("Rate limit reached. Waiting to acquire permit...");
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thread interrupted while waiting for rate limit to"
                        + " reset", e);
                return;
            }
        }
        log.debug("Acquired permit from rate limiter, proceeding with "
                + "execution.");
    }
}
