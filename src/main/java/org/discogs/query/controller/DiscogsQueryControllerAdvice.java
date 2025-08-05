package org.discogs.query.controller;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
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

  // Circuit Breaker Exceptions
  @ExceptionHandler(CallNotPermittedException.class)
  public ResponseEntity<ErrorMessageDTO> handleCircuitBreakerException(final CallNotPermittedException ex) {
    return createErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, 
        "Service temporarily unavailable due to circuit breaker", ex);
  }

  // HTTP Client Exceptions - map to appropriate status codes
  @ExceptionHandler(HttpClientErrorException.class)
  public ResponseEntity<ErrorMessageDTO> handleHttpClientError(final HttpClientErrorException ex) {
    return createErrorResponse(ex.getStatusCode().is4xxClientError() ? 
        HttpStatus.valueOf(ex.getRawStatusCode()) : HttpStatus.INTERNAL_SERVER_ERROR,
        "External API client error: " + ex.getStatusText(), ex);
  }

  @ExceptionHandler(HttpServerErrorException.class)
  public ResponseEntity<ErrorMessageDTO> handleHttpServerError(final HttpServerErrorException ex) {
    return createErrorResponse(HttpStatus.BAD_GATEWAY, 
        "External API server error: " + ex.getStatusText(), ex);
  }

  @ExceptionHandler(ResourceAccessException.class)
  public ResponseEntity<ErrorMessageDTO> handleResourceAccessException(final ResourceAccessException ex) {
    return createErrorResponse(HttpStatus.GATEWAY_TIMEOUT, 
        "External service connection timeout", ex);
  }

  // Validation Exceptions
  @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
  public ResponseEntity<ErrorMessageDTO> handleValidationException(final Exception ex) {
    return createErrorResponse(HttpStatus.BAD_REQUEST, 
        "Invalid request parameters", ex);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorMessageDTO> handleMethodNotSupported(final HttpRequestMethodNotSupportedException ex) {
    return createErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, 
        "HTTP method not supported: " + ex.getMethod(), ex);
  }

  // Application-specific Exceptions
  @ExceptionHandler(DiscogsMarketplaceException.class)
  public ResponseEntity<ErrorMessageDTO> handleMarketplaceException(final DiscogsMarketplaceException ex) {
    return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
        "Marketplace service error", ex);
  }

  @ExceptionHandler(DiscogsSearchException.class)
  public ResponseEntity<ErrorMessageDTO> handleSearchException(final DiscogsSearchException ex) {
    return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
        "Search service error", ex);
  }

  @ExceptionHandler(NoMarketplaceListingsException.class)
  public ResponseEntity<ErrorMessageDTO> handleNoMarketplaceListings(final NoMarketplaceListingsException ex) {
    return createErrorResponse(HttpStatus.NOT_FOUND, 
        "No marketplace listings found", ex);
  }

  // Infrastructure Exceptions  
  @ExceptionHandler(TimeoutException.class)
  public ResponseEntity<ErrorMessageDTO> handleTimeoutException(final TimeoutException ex) {
    return createErrorResponse(HttpStatus.REQUEST_TIMEOUT, 
        "Request processing timeout", ex);
  }

  @ExceptionHandler(IOException.class)
  public ResponseEntity<ErrorMessageDTO> handleIOException(final IOException ex) {
    return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
        "I/O operation failed", ex);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorMessageDTO> handleRuntimeException(final RuntimeException ex) {
    if (ex.getMessage() != null && ex.getMessage().contains("Rate limit exceeded")) {
      return createErrorResponse(HttpStatus.TOO_MANY_REQUESTS, 
          "Rate limit exceeded. Please retry later", ex);
    }
    return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
        "Runtime error occurred", ex);
  }

  // Catch-all for unexpected exceptions
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorMessageDTO> handleGenericException(final Exception ex) {
    return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, 
        "An unexpected error occurred", ex);
  }
}
