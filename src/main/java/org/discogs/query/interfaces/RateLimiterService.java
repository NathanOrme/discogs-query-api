package org.discogs.query.interfaces;

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
}
