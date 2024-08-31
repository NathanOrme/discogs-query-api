package org.discogs.query.client.limits;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimiterTest {

    private RateLimiter rateLimiter;

    @Value("${discogs.rate-limit:60}") // Default rate limit if not set in test environment
    private int rateLimit;

    @BeforeEach
    public void setUp() {
        rateLimiter = new RateLimiter();
        // Set the rate limit for testing
        rateLimiter.maxRequestsPerMinute = rateLimit;
    }

    /**
     * Test that the rate limiter allows the number of requests up to the limit.
     */
    @Test
    void testAllowRequestsWithinLimit() {
        for (int i = 0; i < rateLimit; i++) {
            assertTrue(rateLimiter.tryAcquire(), "Request " + (i + 1) + " should be allowed.");
        }
    }

    /**
     * Test that the rate limiter does not allow more requests than the limit.
     */
    @Test
    void testDenyRequestsBeyondLimit() {
        for (int i = 0; i < rateLimit; i++) {
            assertTrue(rateLimiter.tryAcquire(), "Request " + (i + 1) + " should be allowed.");
        }
        assertFalse(rateLimiter.tryAcquire(), "Should not allow request beyond the limit.");
    }

    /**
     * Test that the rate limiter resets the count after one minute.
     */
    @Test
    @Timeout(value = 70)
    // Adjust timeout as needed
    void testRateLimiterResetsAfterOneMinute() throws InterruptedException {
        for (int i = 0; i < rateLimit; i++) {
            assertTrue(rateLimiter.tryAcquire(), "Request " + (i + 1) + " should be allowed.");
        }
        assertFalse(rateLimiter.tryAcquire(), "Should not allow request beyond the limit.");

        // Wait for rate limiter to reset
        TimeUnit.SECONDS.sleep(65); // Sleep for longer than the reset period

        for (int i = 0; i < rateLimit; i++) {
            assertTrue(rateLimiter.tryAcquire(), "Request " + (i + 1) + " should be allowed after reset.");
        }
    }
}