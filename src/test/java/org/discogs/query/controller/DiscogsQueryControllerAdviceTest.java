package org.discogs.query.controller;

import org.discogs.query.exceptions.DiscogsMarketplaceException;
import org.discogs.query.exceptions.DiscogsSearchException;
import org.discogs.query.model.ErrorMessageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DiscogsQueryControllerAdviceTest {

    private DiscogsQueryControllerAdvice controllerAdvice;

    @BeforeEach
    void setUp() {
        controllerAdvice = new DiscogsQueryControllerAdvice();
    }

    @Test
    void testHandleMarketplaceException() {
        // Arrange
        DiscogsMarketplaceException exception = new DiscogsMarketplaceException("Marketplace error");

        // Act
        ResponseEntity<ErrorMessageDTO> response = controllerAdvice.handleMarketplaceException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Marketplace error", response.getBody().getErrorMessage());
    }

    @Test
    void testHandleSearchException() {
        // Arrange
        DiscogsSearchException exception = new DiscogsSearchException("Search error");

        // Act
        ResponseEntity<ErrorMessageDTO> response = controllerAdvice.handleSearchException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Search error", response.getBody().getErrorMessage());
    }

    @Test
    void testHandleTimeoutException() {
        // Arrange
        TimeoutException exception = new TimeoutException("Timeout occurred");

        // Act
        ResponseEntity<ErrorMessageDTO> response = controllerAdvice.handleTimeoutException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.REQUEST_TIMEOUT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Request took too long to process.", response.getBody().getErrorMessage());
    }
}
