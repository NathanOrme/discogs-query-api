package org.discogs.query.controller;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.exceptions.DiscogsMarketplaceException;
import org.discogs.query.exceptions.DiscogsSearchException;
import org.discogs.query.exceptions.NoMarketplaceListingsException;
import org.discogs.query.model.ErrorMessageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Enhanced global exception handler with proper HTTP status codes and correlation IDs.
 * Provides comprehensive error handling for all application exceptions with appropriate
 * HTTP status codes and structured error responses.
 *
 * @author Nathan Orme
 */
@Slf4j
@ControllerAdvice
public class DiscogsQueryControllerAdvice {

    /**
     * Creates a standardized error response with correlation ID for tracking.
     */
    private ResponseEntity<ErrorMessageDTO> createErrorResponse(
            final HttpStatus status, final String message, final Exception ex) {
        String correlationId = UUID.randomUUID().toString();
        log.error("Error [{}]: {} - {}", correlationId, message, ex.getMessage(), ex);
        return ResponseEntity.status(status)
                .body(new ErrorMessageDTO(message + " (ID: " + correlationId + ")"));
    }

    /**
     * Handles CallNotPermittedException from Resilience4j Circuit Breaker.
     * Maps to HTTP 503 Service Unavailable with a structured error response.
     *
     * @param ex the exception thrown by the circuit breaker
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<ErrorMessageDTO> handleCircuitBreakerException(final CallNotPermittedException ex) {
        return createErrorResponse(HttpStatus.SERVICE_UNAVAILABLE,
                "Service temporarily unavailable due to circuit breaker", ex);
    }

    /**
     * Handles HttpClientErrorException for client-side errors.
     * Maps to appropriate HTTP status codes based on the exception details.
     *
     * @param ex the HttpClientErrorException thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorMessageDTO> handleHttpClientError(final HttpClientErrorException ex) {
        return createErrorResponse(ex.getStatusCode().is4xxClientError() ?
                        HttpStatus.valueOf(ex.getRawStatusCode()) : HttpStatus.INTERNAL_SERVER_ERROR,
                "External API client error: " + ex.getStatusText(), ex);
    }


    /**
     * Handles HttpServerErrorException for server-side errors.
     * Maps to HTTP 502 Bad Gateway or other appropriate status codes.
     *
     * @param ex the HttpServerErrorException thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorMessageDTO> handleHttpServerError(final HttpServerErrorException ex) {
        return createErrorResponse(HttpStatus.BAD_GATEWAY,
                "External API server error: " + ex.getStatusText(), ex);
    }

    /**
     * Handles ResourceAccessException for connection timeouts or unavailable services.
     * Maps to HTTP 504 Gateway Timeout with a structured error response.
     *
     * @param ex the ResourceAccessException thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorMessageDTO> handleResourceAccessException(final ResourceAccessException ex) {
        return createErrorResponse(HttpStatus.GATEWAY_TIMEOUT,
                "External service connection timeout", ex);
    }

    /**
     * Handles validation exceptions for invalid request parameters.
     * Maps to HTTP 400 Bad Request with a structured error response.
     *
     * @param ex the MethodArgumentNotValidException or BindException thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ErrorMessageDTO> handleValidationException(final Exception ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST,
                "Invalid request parameters", ex);
    }

    /**
     * Handles HttpRequestMethodNotSupportedException for unsupported HTTP methods.
     * Maps to HTTP 405 Method Not Allowed with a structured error response.
     *
     * @param ex the HttpRequestMethodNotSupportedException thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorMessageDTO> handleMethodNotSupported(final HttpRequestMethodNotSupportedException ex) {
        return createErrorResponse(HttpStatus.METHOD_NOT_ALLOWED,
                "HTTP method not supported: " + ex.getMethod(), ex);
    }

    /**
     * Handles Discogs-specific exceptions related to marketplace and search operations.
     * Maps to HTTP 500 Internal Server Error with a structured error response.
     *
     * @param ex the DiscogsMarketplaceException or DiscogsSearchException thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(DiscogsMarketplaceException.class)
    public ResponseEntity<ErrorMessageDTO> handleMarketplaceException(final DiscogsMarketplaceException ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Marketplace service error", ex);
    }

    /**
     * Handles DiscogsSearchException for search-related errors.
     * Maps to HTTP 500 Internal Server Error with a structured error response.
     *
     * @param ex the DiscogsSearchException thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(DiscogsSearchException.class)
    public ResponseEntity<ErrorMessageDTO> handleSearchException(final DiscogsSearchException ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Search service error", ex);
    }

    /**
     * Handles NoMarketplaceListingsException when no marketplace listings are found.
     * Maps to HTTP 404 Not Found with a structured error response.
     *
     * @param ex the NoMarketplaceListingsException thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(NoMarketplaceListingsException.class)
    public ResponseEntity<ErrorMessageDTO> handleNoMarketplaceListings(final NoMarketplaceListingsException ex) {
        return createErrorResponse(HttpStatus.NOT_FOUND,
                "No marketplace listings found", ex);
    }

    /**
     * Handles TimeoutException for request processing timeouts.
     * Maps to HTTP 408 Request Timeout with a structured error response.
     *
     * @param ex the TimeoutException thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ErrorMessageDTO> handleTimeoutException(final TimeoutException ex) {
        return createErrorResponse(HttpStatus.REQUEST_TIMEOUT,
                "Request processing timeout", ex);
    }

    /**
     * Handles IOException for I/O operation failures.
     * Maps to HTTP 500 Internal Server Error with a structured error response.
     *
     * @param ex the IOException thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorMessageDTO> handleIOException(final IOException ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "I/O operation failed", ex);
    }

    /**
     * Handles RuntimeException for general runtime errors.
     * Maps to HTTP 500 Internal Server Error with a structured error response.
     *
     * @param ex the RuntimeException thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessageDTO> handleRuntimeException(final RuntimeException ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("Rate limit exceeded")) {
            return createErrorResponse(HttpStatus.TOO_MANY_REQUESTS,
                    "Rate limit exceeded. Please retry later", ex);
        }
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Runtime error occurred", ex);
    }

    /**
     * Handles generic exceptions that are not specifically handled by other methods.
     * Maps to HTTP 500 Internal Server Error with a structured error response.
     *
     * @param ex the Exception thrown
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDTO> handleGenericException(final Exception ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred", ex);
    }
}
