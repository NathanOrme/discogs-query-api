package org.discogs.query.interfaces;

import java.util.concurrent.Callable;

/**
 * Service interface for handling retries.
 * <p>
 * This interface defines methods for executing actions with retry logic.
 */
public interface RetryService {

    /**
     * Executes a callable action with retry logic.
     * <p>
     * This method retries the action if it fails, up to a maximum number of
     * attempts.
     *
     * @param action            the callable action to be executed
     * @param actionDescription a description of the action being performed
     * @param <T>               the type of the result returned by the action
     * @return the result of the action
     * @throws Exception if the action fails after all retry attempts
     */
    <T> T executeWithRetry(Callable<T> action, String actionDescription) throws Exception;
}
