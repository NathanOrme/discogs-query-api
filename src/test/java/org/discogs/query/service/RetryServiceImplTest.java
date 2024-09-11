package org.discogs.query.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RetryServiceImplTest {

    private RetryServiceImpl retryService;

    @BeforeEach
    void setUp() {
        retryService = new RetryServiceImpl();
    }

    @Test
    void testExecuteWithRetry_SuccessOnFirstAttempt() throws Exception {
        // Arrange
        Callable<String> mockAction = mock(Callable.class);
        when(mockAction.call()).thenReturn("Success");

        // Act
        String result = retryService.executeWithRetry(mockAction, "Test Action");

        // Assert
        assertEquals("Success", result);
        verify(mockAction, times(1)).call(); // Should be called once, no retries needed
    }

    @Test
    void testExecuteWithRetry_SuccessOnRetry() throws Exception {
        // Arrange
        Callable<String> mockAction = mock(Callable.class);
        when(mockAction.call())
                .thenThrow(new RuntimeException("First Failure"))
                .thenReturn("Success");

        // Act
        String result = retryService.executeWithRetry(mockAction, "Test Action");

        // Assert
        assertEquals("Success", result);
        verify(mockAction, times(2)).call(); // Should be called twice, once for failure and once for success
    }

    @Test
    void testExecuteWithRetry_ExceedsRetryLimit() throws Exception {
        // Arrange
        Callable<String> mockAction = mock(Callable.class);
        when(mockAction.call()).thenThrow(new RuntimeException("Failure"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                retryService.executeWithRetry(mockAction, "Test Action")
        );

        assertEquals("Failure", exception.getMessage());
        verify(mockAction, times(3)).call(); // Should be called 3 times before giving up
    }

    @Test
    void testExecuteWithRetry_429TooManyRequestsException() throws Exception {
        // Arrange
        Callable<String> mockAction = mock(Callable.class);
        HttpClientErrorException tooManyRequestsException = new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS);
        when(mockAction.call())
                .thenThrow(new RuntimeException("Failure", tooManyRequestsException));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                retryService.executeWithRetry(mockAction, "Test Action")
        );

        assertInstanceOf(HttpClientErrorException.class, exception.getCause());
        verify(mockAction, times(3)).call(); // Should be called 3 times
    }

    @Test
    void testIs429StatusCodeException() {
        // Arrange
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS);

        // Act
        boolean result = retryService.is429StatusCodeException(new Exception(exception));

        // Assert
        assertTrue(result);
    }

    @Test
    void testIs429StatusCodeException_OtherException() {
        // Act
        boolean result = retryService.is429StatusCodeException(new Exception(new RuntimeException()));

        // Assert
        assertFalse(result);
    }
}

