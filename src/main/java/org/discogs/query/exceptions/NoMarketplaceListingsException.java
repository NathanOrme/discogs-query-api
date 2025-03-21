package org.discogs.query.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception class for handling errors that occur when interacting with the Discogs API. This
 * exception is used to signify an internal server error (HTTP 500) in the application.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoMarketplaceListingsException extends RuntimeException {
    /**
     * Constructs a new {@link DiscogsSearchException} with the specified detail message.
     *
     * @param message the detail message for this exception
     */
    public NoMarketplaceListingsException(final String message) {
        super(message);
    }

    /**
     * Constructs a new {@link DiscogsSearchException} with the specified detail message and cause.
     *
     * @param message the detail message for this exception
     * @param e       the cause of this exception (a {@link Exception} that triggered this exception)
     */
    public NoMarketplaceListingsException(final String message, final Exception e) {
        super(message, e);
    }
}
