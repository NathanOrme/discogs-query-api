package org.discogs.query.service.requests;

import lombok.extern.slf4j.Slf4j;
import org.discogs.query.helpers.LogHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Circuit breaker implementation to handle persistent failures from external services. Follows the
 * circuit breaker pattern with CLOSED, OPEN, and HALF_OPEN states.
 */
@Slf4j
@Component
public class CircuitBreakerService {

    /**
     * Enum representing the states of the circuit breaker.
     */
    public enum State {
        /** Circuit breaker allows all requests to pass through. */
        CLOSED,
        /** Circuit breaker blocks all requests. */
        OPEN,
        /** Circuit breaker allows limited requests to test if service has recovered. */
        HALF_OPEN
    }

    /**
     * Exception thrown when the circuit breaker is open and a call is attempted.
     */
    public static class CircuitBreakerOpenException extends RuntimeException {
        /**
         * Creates a new CircuitBreakerOpenException with the specified message.
         *
         * @param message the exception message
         */
        public CircuitBreakerOpenException(final String message) {
            super(message);
        }
    }

    /**
     * Circuit breaker state management.
     */
    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
    /**
     * Counters for tracking failures
     */
    private final AtomicInteger failureCount = new AtomicInteger(0);
    /**
     * Counter for tracking successful calls in HALF_OPEN state
     */
    private final AtomicInteger successCount = new AtomicInteger(0);
    /**
     * Timestamp of the last failure to determine when to reset the circuit breaker.
     */
    private final AtomicLong lastFailureTime = new AtomicLong(0);

    /**
     * Configuration properties for the circuit breaker.
     */
    @Value("${circuit-breaker.failure-threshold:5}")
    private int failureThreshold;

    /**
     * Timeout duration for the circuit breaker to reset from OPEN to HALF_OPEN state.
     */
    @Value("${circuit-breaker.timeout-duration:60000}")
    private long timeoutDuration; // milliseconds

    /**
     * Maximum number of successful calls required to reset the circuit breaker from HALF_OPEN to
     * CLOSED state.
     */
    @Value("${circuit-breaker.half-open-max-calls:3}")
    private int halfOpenMaxCalls;

    /**
     * Executes an operation within the circuit breaker.
     *
     * @param operation the operation to execute
     * @param <T>       the return type of the operation
     * @return the result of the operation
     * @throws Exception if the operation fails or circuit is open
     */
    public <T> T execute(final OperationWithException<T> operation) throws Exception {
        State currentState = state.get();

        if (currentState == State.OPEN) {
            if (shouldAttemptReset()) {
                state.set(State.HALF_OPEN);
                successCount.set(0);
                LogHelper.info(() -> "Circuit breaker transitioning from OPEN to HALF_OPEN");
            } else {
                LogHelper.warn(() -> "Circuit breaker is OPEN - rejecting call");
                throw new CircuitBreakerOpenException("Circuit breaker is OPEN");
            }
        }

        try {
            T result = operation.execute();
            onSuccess();
            return result;
        } catch (final Exception e) {
            onFailure();
            throw e;
        }
    }

    /**
     * Records a successful operation.
     */
    private void onSuccess() {
        State currentState = state.get();

        if (currentState == State.HALF_OPEN) {
            int successes = successCount.incrementAndGet();
            if (successes >= halfOpenMaxCalls) {
                reset();
                LogHelper.info(
                        () -> "Circuit breaker reset to CLOSED after {} successful calls", successes);
            }
        } else if (currentState == State.CLOSED) {
            failureCount.set(0);
        }
    }

    /**
     * Records a failed operation.
     */
    private void onFailure() {
        lastFailureTime.set(System.currentTimeMillis());
        int failures = failureCount.incrementAndGet();

        State currentState = state.get();

        if (currentState == State.HALF_OPEN
                || (currentState == State.CLOSED && failures >= failureThreshold)) {
            state.set(State.OPEN);
            LogHelper.warn(() -> "Circuit breaker opening after {} failures", failures);
        }
    }

    /**
     * Resets the circuit breaker to CLOSED state.
     */
    private void reset() {
        state.set(State.CLOSED);
        failureCount.set(0);
        successCount.set(0);
    }

    /**
     * Determines if the circuit breaker should attempt to reset from OPEN to HALF_OPEN.
     *
     * @return true if timeout period has elapsed
     */
    private boolean shouldAttemptReset() {
        return System.currentTimeMillis() - lastFailureTime.get() >= timeoutDuration;
    }

    /**
     * Gets the current state of the circuit breaker.
     *
     * @return the current state
     */
    public State getState() {
        return state.get();
    }

    /**
     * Gets the current failure count.
     *
     * @return the failure count
     */
    public int getFailureCount() {
        return failureCount.get();
    }

    /**
     * Functional interface for operations that can throw exceptions.
     *
     * @param <T> the return type
     */
    @FunctionalInterface
    public interface OperationWithException<T> {
        /**
         * Executes the operation that may throw an exception.
         *
         * @return the result of the operation
         * @throws Exception if the operation fails
         */
        T execute() throws Exception;
    }
}
