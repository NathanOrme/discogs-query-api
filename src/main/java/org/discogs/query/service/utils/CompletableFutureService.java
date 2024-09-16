package org.discogs.query.service.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Utility class for handling CompletableFuture tasks with timeout management and logging.
 */
@Slf4j
@Service
public class CompletableFutureService {

    @Value("${queries.timeout:50}")
    private int timeoutInSeconds;

    /**
     * Processes a list of CompletableFutures with a specified timeout.
     *
     * @param futures the list of CompletableFuture tasks
     * @param <T>     the type of result expected
     * @return a list of results, with failed or timed-out results filtered out
     */
    public <T> List<T> processFuturesWithTimeout(final List<CompletableFuture<T>> futures) {
        return futures.stream()
                .map(future -> getFutureResultWithTimeout(future, timeoutInSeconds))
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Retrieves the result of a CompletableFuture with a specified timeout.
     *
     * @param future           the CompletableFuture to retrieve the result from
     * @param timeoutInSeconds the timeout in seconds
     * @param <T>              the type of result expected
     * @return the result if available within the timeout, or null if timed out or failed
     */
    private <T> T getFutureResultWithTimeout(final CompletableFuture<T> future, final long timeoutInSeconds) {
        try {
            return future.get(timeoutInSeconds, TimeUnit.SECONDS);
        } catch (final InterruptedException | TimeoutException e) {
            log.warn("Task timed out or interrupted");
            future.cancel(true); // Cancel the task if it times out
            return null;
        } catch (final Exception e) {
            log.error("Error processing future", e);
            return null;
        }
    }
}

