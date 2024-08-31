package org.discogs.query.interfaces;

public interface RateLimiterService {
    void waitForRateLimit();
}
