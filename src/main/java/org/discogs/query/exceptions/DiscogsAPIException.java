package org.discogs.query.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DiscogsAPIException extends RuntimeException {

    public DiscogsAPIException(final String message, final Exception e) {
        super(message, e);
    }
}
