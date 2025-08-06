package org.discogs.query.service.requests;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.helpers.LogHelper;
import org.discogs.query.interfaces.RetryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

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
    Exception lastException = null;
    
    while (isAttemptNumberLessThanMaximum(attempt)) {
      try {
        LogHelper.info(
            () -> "Attempting {}. Attempt {} of {}", actionDescription, attempt, RETRY_COUNT);
        return action.call();
      } catch (final Exception e) {
        lastException = e;
        
        // Check if this is a non-retryable error
        if (!isRetryableException(e)) {
          LogHelper.debug(() -> "Non-retryable exception encountered, exiting retry logic: {}", e.getMessage());
          throw e;
        }
        
        // Log the attempt
        LogHelper.warn(
            () -> "Error during {} on attempt {} of {}. Exception: {}",
            actionDescription,
            attempt,
            RETRY_COUNT,
            e.getMessage());
        
        // If this was the last attempt, throw the exception
        if (attempt == RETRY_COUNT) {
          LogHelper.error(() -> "All {} retry attempts exhausted for {}", RETRY_COUNT, actionDescription);
          throw e;
        }
        
        // Delay before retry
        delayThreadBasedOnException(e, attempt);
        attempt++;
      }
    }
    
    // This should never be reached, but adding for safety
    if (lastException != null) {
      throw lastException;
    }
    throw new IllegalStateException("Retry logic reached unexpected state.");
  }

  /**
   * Determines if an exception should trigger a retry.
   * 
   * @param e the exception to evaluate
   * @return true if the exception is transient and should be retried
   */
  private boolean isRetryableException(final Exception e) {
    // Non-retryable client errors (4xx except 408, 429)
    if (e instanceof HttpClientErrorException clientException) {
      var statusCode = clientException.getStatusCode();
      return statusCode.value() == HttpStatus.REQUEST_TIMEOUT.value() || 
             statusCode.value() == HttpStatus.TOO_MANY_REQUESTS.value() ||
             statusCode.value() == HttpStatus.LOCKED.value(); // 423 can sometimes be temporary
    }
    
    // Server errors (5xx) are generally retryable
    if (e instanceof HttpServerErrorException) {
      return true;
    }
    
    // Network-related exceptions are retryable
    if (e instanceof ResourceAccessException || 
        e instanceof SocketTimeoutException ||
        e instanceof IOException) {
      return true;
    }
    
    // Check for specific nested exceptions
    Throwable cause = e.getCause();
    if (cause instanceof SocketTimeoutException ||
        cause instanceof IOException ||
        (cause instanceof HttpClientErrorException clientException && 
         (clientException.getStatusCode().value() == HttpStatus.REQUEST_TIMEOUT.value() ||
          clientException.getStatusCode().value() == HttpStatus.TOO_MANY_REQUESTS.value()))) {
      return true;
    }
    
    // Default to non-retryable for unknown exceptions
    return false;
  }

  private void delayThreadBasedOnException(final Exception e, final int attempt) throws InterruptedException {
    long delay = RETRY_DELAY;
    
    if (is429StatusCodeException(e)) {
      LogHelper.info(() -> "429 Status Code Received - Using extended delay");
      delay = 30; // Long delay for rate limiting
    } else if (isServerErrorException(e)) {
      // Exponential backoff for server errors
      delay = (long) (RETRY_DELAY * Math.pow(2, attempt - 1));
      LogHelper.info(() -> "Server error detected - Using exponential backoff: {} seconds", delay);
    } else {
      LogHelper.info(() -> "Using standard retry delay: {} seconds", delay);
    }
    
    TimeUnit.SECONDS.sleep(delay);
  }

  boolean is429StatusCodeException(final Exception e) {
    if (e instanceof HttpClientErrorException clientException) {
      return clientException.getStatusCode().value() == HttpStatus.TOO_MANY_REQUESTS.value();
    }
    
    Throwable cause = e.getCause();
    return cause instanceof HttpClientErrorException clientException &&
           clientException.getStatusCode().value() == HttpStatus.TOO_MANY_REQUESTS.value();
  }
  
  private boolean isServerErrorException(final Exception e) {
    return e instanceof HttpServerErrorException ||
           (e.getCause() instanceof HttpServerErrorException);
  }
}
