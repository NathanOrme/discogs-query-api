package org.discogs.query.service.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.interfaces.RateLimiterService;
import org.discogs.query.limits.RateLimiter;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimiterServiceImpl implements RateLimiterService {

    private final RateLimiter rateLimiter;

    @Override
    public void waitForRateLimit() {
        if (log.isDebugEnabled()) {
            log.debug("Starting to check rate limiter status...");
        }

        while (!rateLimiter.tryAcquire()) {
            try {
                log.info("Rate limit reached. Waiting to acquire permit...");
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                if (log.isErrorEnabled()) {
                    log.error("Thread interrupted while waiting for rate limit to reset", e);
                }
                return;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Acquired permit from rate limiter, proceeding with execution.");
        }
    }
}
