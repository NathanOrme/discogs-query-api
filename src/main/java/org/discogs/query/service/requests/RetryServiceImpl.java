package org.discogs.query.service.requests;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.helpers.LogHelper;
import org.discogs.query.interfaces.RetryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Component
public class RetryServiceImpl implements RetryService {

  private static final int RETRY_COUNT = 3;
  private static final long RETRY_DELAY = 2; // in seconds

  private static boolean isAttemptNumberLessThanMaximum(final int attempt) {
    return attempt <= RETRY_COUNT;
  }

  @Override
  public <T> T executeWithRetry(final Callable<T> action, final String actionDescription)
      throws Exception {
    int attempt = 1;
    while (isAttemptNumberLessThanMaximum(attempt)) {
      try {
        LogHelper.info(
            () -> "Attempting {}. Attempt {} of {}", actionDescription, attempt, RETRY_COUNT);
        return action.call();
      } catch (final Exception e) {
        attempt = handleRetryCount(actionDescription, e, attempt);
        delayThreadBasedOnStatusCode(e);
      }
    }
    throw new IllegalStateException("Retry logic should never reach here.");
  }

  private int handleRetryCount(final String actionDescription, final Exception e, int attempt)
      throws Exception {
    LogHelper.warn(
        () -> "Error during {} on attempt {} of {}. Exception: {}",
        actionDescription,
        attempt,
        RETRY_COUNT,
        e.getMessage());
    if (e.getCause() instanceof final HttpClientErrorException httpClientErrorException
        && httpClientErrorException.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
      LogHelper.debug(() -> "404 received, exiting retry logic");
      throw e;
    }

    if (attempt == RETRY_COUNT) {
      throw e;
    }
    attempt++;
    return attempt;
  }

  private void delayThreadBasedOnStatusCode(final Exception e) throws InterruptedException {
    if (is429StatusCodeException(e)) {
      LogHelper.info(() -> "429 Status Code Received - Sleeping for 30 seconds");
      TimeUnit.SECONDS.sleep(30);
    } else {
      LogHelper.info(() -> "Sleeping for {} seconds", RETRY_DELAY);
      TimeUnit.SECONDS.sleep(RETRY_DELAY);
    }
  }

  boolean is429StatusCodeException(final Exception e) {
    return e.getCause() instanceof final HttpClientErrorException httpClientErrorException
        && httpClientErrorException.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS);
  }
}
