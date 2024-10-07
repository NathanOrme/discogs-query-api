package org.discogs.query.controller;

import org.discogs.query.exceptions.DiscogsMarketplaceException;
import org.discogs.query.exceptions.DiscogsSearchException;
import org.discogs.query.model.ErrorMessageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Global exception handler for the Discogs Query application.
 * Handles exceptions thrown by controllers and provides appropriate HTTP responses.
 *
 * <p>This class centralizes the handling of exceptions across all controllers
 * in the application, returning consistent error responses with proper
 * HTTP status codes and error messages.
 *
 * <p>Annotations:
 * <ul>
 *  <li>{@link ControllerAdvice} - Declares this class as an exception handler across all controllers.</li>
 *  <li>{@link ExceptionHandler} - Specifies which exceptions are handled by each method.</li>
 * </ul>
 *
 * <p>Handles the following exceptions:
 * <ul>
 *  <li>{@link DiscogsMarketplaceException} - Returns 500 Internal Server Error.</li>
 *  <li>{@link DiscogsSearchException} - Returns 500 Internal Server Error.</li>
 *  <li>{@link TimeoutException} - Returns 408 Request Timeout.</li>
 *  <li>{@link IOException} - Returns 500 Internal Server Error.</li>
 *  <li>{@link Exception} - Returns 500 Internal Server Error for unexpected errors.</li>
 * </ul>
 *
 * @author Nathan Orme
 */
@ControllerAdvice
public class DiscogsQueryControllerAdvice {

    /**
     * Handles {@link DiscogsMarketplaceException} and returns a {@link ResponseEntity}
     * with a 500 Internal Server Error status and an error message.
     *
     * @param ex the {@link DiscogsMarketplaceException} to handle
     * @return a {@link ResponseEntity} containing the error message and HTTP status
     */
    @ExceptionHandler(DiscogsMarketplaceException.class)
    public ResponseEntity<ErrorMessageDTO> handleMarketplaceException(final DiscogsMarketplaceException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessageDTO(ex.getMessage()));
    }

    /**
     * Handles {@link DiscogsSearchException} and returns a {@link ResponseEntity}
     * with a 500 Internal Server Error status and an error message.
     *
     * @param ex the {@link DiscogsSearchException} to handle
     * @return a {@link ResponseEntity} containing the error message and HTTP status
     */
    @ExceptionHandler(DiscogsSearchException.class)
    public ResponseEntity<ErrorMessageDTO> handleSearchException(final DiscogsSearchException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessageDTO(ex.getMessage()));
    }

    /**
     * Handles {@link TimeoutException} and returns a {@link ResponseEntity}
     * with a 408 Request Timeout status and a custom error message.
     *
     * @param ex the {@link TimeoutException} to handle
     * @return a {@link ResponseEntity} containing the custom timeout message and HTTP status
     */
    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ErrorMessageDTO> handleTimeoutException(final TimeoutException ex) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(new ErrorMessageDTO(
                "Request took too long to process."));
    }

    /**
     * Handles {@link IOException} and returns a {@link ResponseEntity}
     * with a 500 Internal Server Error status and a custom error message.
     *
     * @param ex the {@link IOException} to handle
     * @return a {@link ResponseEntity} containing the error message and HTTP status
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorMessageDTO> handleIOException(final IOException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessageDTO(ex.getMessage()));
    }

    /**
     * Handles {@link Exception} and returns a {@link ResponseEntity}
     * with a 500 Internal Server Error status and a custom error message.
     *
     * @param ex the {@link Exception} to handle
     * @return a {@link ResponseEntity} containing the error message and HTTP status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDTO> handleException(final Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessageDTO(
                "An unexpected error occurred: %s".formatted(ex.getMessage())));
    }
}
