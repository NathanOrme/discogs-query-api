package org.discogs.query.service;

import org.discogs.query.interfaces.RateLimiterService;
import org.discogs.query.limits.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RateLimiterServiceImplTest {

    private RateLimiterService rateLimiterService;
    private RateLimiter rateLimiter;

    @BeforeEach
    public void setUp() {
        rateLimiter = mock(RateLimiter.class);
        rateLimiterService = new RateLimiterServiceImpl(rateLimiter);
    }

    @Test
    void testWaitForRateLimit() {
        when(rateLimiter.tryAcquire()).thenReturn(false).thenReturn(true);

        rateLimiterService.waitForRateLimit();

        verify(rateLimiter, times(2)).tryAcquire();
    }

    @Test
    void testRateLimiterImmediateAcquire() {
        when(rateLimiter.tryAcquire()).thenReturn(true);

        rateLimiterService.waitForRateLimit();

        verify(rateLimiter, times(1)).tryAcquire();
    }
}
