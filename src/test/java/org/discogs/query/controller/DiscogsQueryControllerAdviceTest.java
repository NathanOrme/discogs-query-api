package org.discogs.query.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.discogs.query.exceptions.DiscogsMarketplaceException;
import org.discogs.query.exceptions.DiscogsSearchException;
import org.discogs.query.model.ErrorMessageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    ResponseEntity<ErrorMessageDTO> response =
        controllerAdvice.handleMarketplaceException(exception);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().errorMessage().startsWith("Marketplace service error"));
    assertTrue(response.getBody().errorMessage().contains("(ID:"));
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
    assertTrue(response.getBody().errorMessage().startsWith("Search service error"));
    assertTrue(response.getBody().errorMessage().contains("(ID:"));
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
    assertTrue(response.getBody().errorMessage().startsWith("Request processing timeout"));
    assertTrue(response.getBody().errorMessage().contains("(ID:"));
  }

  @Test
  void testHandleIOException() {
    // Arrange
    IOException exception = new IOException("IO error occurred");

    // Act
    ResponseEntity<ErrorMessageDTO> response = controllerAdvice.handleIOException(exception);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().errorMessage().startsWith("I/O operation failed"));
    assertTrue(response.getBody().errorMessage().contains("(ID:"));
  }

  @Test
  void testHandleGenericException() {
    // Arrange
    Exception exception = new Exception("Generic error occurred");

    // Act
    ResponseEntity<ErrorMessageDTO> response = controllerAdvice.handleGenericException(exception);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().errorMessage().startsWith("An unexpected error occurred"));
    assertTrue(response.getBody().errorMessage().contains("(ID:"));
  }
}
