package org.discogs.query.service.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.helpers.LogHelper;
import org.discogs.query.interfaces.RateLimiterService;
import org.discogs.query.limits.RateLimiter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimiterServiceImpl implements RateLimiterService {

  private final RateLimiter rateLimiter;

  @Override
  public void waitForRateLimit() {
    LogHelper.debug(() -> "Checking rate limiter status...");

    if (rateLimiter.tryAcquire()) {
      LogHelper.debug(() -> "Acquired permit from rate limiter, proceeding with execution.");
    } else {
      LogHelper.warn(
          () -> "Rate limit exceeded. Request denied. Available tokens: {}",
          rateLimiter.getAvailableTokens());
      throw new RuntimeException("Rate limit exceeded. Please retry later.");
    }
  }
}
