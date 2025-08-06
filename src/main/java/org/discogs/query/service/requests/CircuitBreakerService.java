package org.discogs.query.service.requests;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.helpers.LogHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Circuit breaker implementation to handle persistent failures from external services.
 * Follows the circuit breaker pattern with CLOSED, OPEN, and HALF_OPEN states.
 */
@Slf4j
@Component
public class CircuitBreakerService {

  public enum State {
    CLOSED, OPEN, HALF_OPEN
  }

  public static class CircuitBreakerOpenException extends RuntimeException {
    public CircuitBreakerOpenException(String message) {
      super(message);
    }
  }

  private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
  private final AtomicInteger failureCount = new AtomicInteger(0);
  private final AtomicInteger successCount = new AtomicInteger(0);
  private final AtomicLong lastFailureTime = new AtomicLong(0);

  @Value("${circuit-breaker.failure-threshold:5}")
  private int failureThreshold;

  @Value("${circuit-breaker.timeout-duration:60000}")
  private long timeoutDuration; // milliseconds

  @Value("${circuit-breaker.half-open-max-calls:3}")
  private int halfOpenMaxCalls;

  /**
   * Executes an operation within the circuit breaker.
   * 
   * @param operation the operation to execute
   * @param <T> the return type of the operation
   * @return the result of the operation
   * @throws Exception if the operation fails or circuit is open
   */
  public <T> T execute(OperationWithException<T> operation) throws Exception {
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
    } catch (Exception e) {
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
        LogHelper.info(() -> "Circuit breaker reset to CLOSED after {} successful calls", successes);
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
    
    if (currentState == State.HALF_OPEN || 
        (currentState == State.CLOSED && failures >= failureThreshold)) {
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
    T execute() throws Exception;
  }
}