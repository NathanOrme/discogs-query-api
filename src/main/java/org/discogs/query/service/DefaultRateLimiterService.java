package org.discogs.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.client.limits.RateLimiter;
import org.discogs.query.interfaces.RateLimiterService;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultRateLimiterService implements RateLimiterService {

    private final RateLimiter rateLimiter;

    @Override
    public void waitForRateLimit() {
        while (!rateLimiter.tryAcquire()) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Interrupted while waiting for rate limit", e);
            }
        }
    }
}
