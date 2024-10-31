package org.discogs.query.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom Exception for when normalization fails
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class NormalizedException extends RuntimeException {

    /**
     * Constructor for normalization exception
     *
     * @param message Message for the exception
     * @param e       Exception that was originally thrown
     */
    public NormalizedException(final String message, final Exception e) {
        super(message, e);
    }
}
