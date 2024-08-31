package org.discogs.query.service;

import lombok.extern.slf4j.Slf4j;
import org.discogs.query.interfaces.RetryService;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DefaulRetryService implements RetryService {

    private static final int RETRY_COUNT = 3;
    private static final long RETRY_DELAY = 2; // in seconds

    @Override
    public <T> T executeWithRetry(final Callable<T> action, final String actionDescription) throws Exception {
        int attempt = 1;
        while (attempt <= RETRY_COUNT) {
            try {
                log.info(String.format("Attempting %s. Attempt %d of %d", actionDescription, attempt, RETRY_COUNT));
                return action.call();
            } catch (final Exception e) {
                log.warn(String.format("Error during %s on attempt %d of %d. Exception: %s",
                        actionDescription, attempt, RETRY_COUNT, e.getMessage()));
                if (attempt == RETRY_COUNT) {
                    throw e; // rethrow after final attempt
                }
                attempt++;
                TimeUnit.SECONDS.sleep(RETRY_DELAY);
            }
        }
        throw new IllegalStateException("Retry logic should never reach here.");
    }
}
