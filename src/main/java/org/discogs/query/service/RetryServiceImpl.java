package org.discogs.query.service;

import lombok.extern.slf4j.Slf4j;
import org.discogs.query.interfaces.RetryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RetryServiceImpl implements RetryService {

    private static final int RETRY_COUNT = 3;
    private static final long RETRY_DELAY = 2; // in seconds

    @Override
    public <T> T executeWithRetry(final Callable<T> action, final String actionDescription) throws Exception {
        int attempt = 1;
        while (attempt <= RETRY_COUNT) {
            try {
                log.info("Attempting {}. Attempt {} of {}", actionDescription, attempt, RETRY_COUNT);
                return action.call();
            } catch (final Exception e) {
                log.warn("Error during {} on attempt {} of {}. Exception: {}",
                        actionDescription, attempt, RETRY_COUNT, e.getMessage());
                if (attempt == RETRY_COUNT) {
                    throw e; // rethrow after final attempt
                }
                attempt++;
                if (is429StatusCodeException(e)) {
                    log.info("429 Status Code Received - Sleeping for a minute");
                    TimeUnit.MINUTES.sleep(1);
                } else {
                    log.info("Sleeping for {} seconds", RETRY_DELAY);
                    TimeUnit.SECONDS.sleep(RETRY_DELAY);
                }
            }
        }
        throw new IllegalStateException("Retry logic should never reach here.");
    }

    private boolean is429StatusCodeException(final Exception e) {
        if (!(e.getCause() instanceof final HttpClientErrorException httpClientErrorException)) {
            return false;
        }

        return httpClientErrorException.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS);
    }
}
