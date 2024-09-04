package org.discogs.query.service;

import lombok.extern.slf4j.Slf4j;
import org.discogs.query.interfaces.RetryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the {@link RetryService} that provides retry logic for
 * executing
 * operations that may fail transiently.
 * <p>
 * This implementation will retry the given action a specified number of
 * times with delays
 * between attempts. It handles the specific case where HTTP status code 429
 * (Too Many Requests)
 * is encountered by sleeping for a longer period.
 */
@Slf4j
@Component
public class RetryServiceImpl implements RetryService {

    /**
     * The maximum number of retry attempts.
     */
    private static final int RETRY_COUNT = 3;

    /**
     * The delay between retry attempts, in seconds.
     */
    private static final long RETRY_DELAY = 2; // in seconds

    /**
     * Executes the given action with retry logic.
     * <p>
     * This method will attempt to execute the provided {@link Callable}
     * action up to {@value #RETRY_COUNT}
     * times. If an exception occurs, it will retry after a short delay. If
     * the exception is caused by
     * an HTTP 429 status code, it will sleep for a longer period (1 minute)
     * before retrying.
     *
     * @param action            the {@link Callable} action to execute
     * @param actionDescription a description of the action being attempted,
     *                          used for logging
     * @param <T>               the return type of the {@link Callable} action
     * @return the result of the {@link Callable} action
     * @throws Exception if the action fails after the maximum number of retries
     */
    @Override
    public <T> T executeWithRetry(final Callable<T> action,
                                  final String actionDescription) throws Exception {
        int attempt = 1;
        while (attempt <= RETRY_COUNT) {
            try {
                log.info("Attempting {}. Attempt {} of {}", actionDescription
                        , attempt, RETRY_COUNT);
                return action.call();
            } catch (final Exception e) {
                log.warn("Error during {} on attempt {} of {}. Exception: {}",
                        actionDescription, attempt, RETRY_COUNT,
                        e.getMessage());
                if (attempt == RETRY_COUNT) {
                    throw e; // rethrow after final attempt
                }
                attempt++;
                if (is429StatusCodeException(e)) {
                    log.info("429 Status Code Received - Sleeping for a " +
                            "minute");
                    TimeUnit.MINUTES.sleep(1);
                } else {
                    log.info("Sleeping for {} seconds", RETRY_DELAY);
                    TimeUnit.SECONDS.sleep(RETRY_DELAY);
                }
            }
        }
        throw new IllegalStateException("Retry logic should never reach here.");
    }

    /**
     * Checks if the given exception was caused by an HTTP 429 (Too Many
     * Requests) error.
     *
     * @param e the exception to check
     * @return true if the exception was caused by an HTTP 429 error, false
     * otherwise
     */
    private boolean is429StatusCodeException(final Exception e) {
        if (!(e.getCause() instanceof final HttpClientErrorException httpClientErrorException)) {
            return false;
        }

        return httpClientErrorException.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS);
    }
}
