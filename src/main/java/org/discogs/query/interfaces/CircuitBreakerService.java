package org.discogs.query.interfaces;

/**
 * Interface for circuit breaker implementation to handle persistent failures from external
 * services. Follows the circuit breaker pattern with CLOSED, OPEN, and HALF_OPEN states.
 */
public interface CircuitBreakerService {

  /** Enum representing the states of the circuit breaker. */
  enum State {
    /** Circuit breaker allows all requests to pass through. */
    CLOSED,
    /** Circuit breaker blocks all requests. */
    OPEN,
    /** Circuit breaker allows limited requests to test if service has recovered. */
    HALF_OPEN
  }

  /** Exception thrown when the circuit breaker is open and a call is attempted. */
  class CircuitBreakerOpenException extends RuntimeException {
    /**
     * Creates a new CircuitBreakerOpenException with the specified message.
     *
     * @param message the exception message
     */
    public CircuitBreakerOpenException(String message) {
      super(message);
    }
  }

  /**
   * Executes an operation within the circuit breaker.
   *
   * @param operation the operation to execute
   * @param <T> the return type of the operation
   * @return the result of the operation
   * @throws Exception if the operation fails or circuit is open
   */
  <T> T execute(OperationWithException<T> operation) throws Exception;

  /**
   * Gets the current state of the circuit breaker.
   *
   * @return the current state
   */
  State getState();

  /**
   * Gets the current failure count.
   *
   * @return the failure count
   */
  int getFailureCount();

  /**
   * Functional interface for operations that can throw exceptions.
   *
   * @param <T> the return type
   */
  @FunctionalInterface
  interface OperationWithException<T> {
    /**
     * Executes the operation that may throw an exception.
     *
     * @return the result of the operation
     * @throws Exception if the operation fails
     */
    T execute() throws Exception;
  }
}