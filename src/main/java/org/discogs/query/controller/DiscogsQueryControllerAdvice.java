package org.discogs.query.controller;

import org.discogs.query.exceptions.DiscogsMarketplaceException;
import org.discogs.query.exceptions.DiscogsSearchException;
import org.discogs.query.model.ErrorMessageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.concurrent.TimeoutException;

@ControllerAdvice
public class DiscogsQueryControllerAdvice {

    @ExceptionHandler(DiscogsMarketplaceException.class)
    public ResponseEntity<ErrorMessageDTO> handleMarketplaceException(final DiscogsMarketplaceException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessageDTO(ex.getMessage()));
    }

    @ExceptionHandler(DiscogsSearchException.class)
    public ResponseEntity<ErrorMessageDTO> handleSearchException(final DiscogsSearchException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessageDTO(ex.getMessage()));
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ErrorMessageDTO> handleTimeoutException(final TimeoutException ex) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(new ErrorMessageDTO("Request took too long to " +
                "process."));
    }

}
