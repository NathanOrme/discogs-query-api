package org.discogs.query.service;

import org.discogs.query.interfaces.RetryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DefaultRetryServiceTest {

    private RetryService retryService;

    @BeforeEach
    public void setUp() {
        retryService = new RetryServiceImpl();
    }

    @Test
    void testSuccessfulExecution() throws Exception {
        Callable<String> action = mock(Callable.class);
        when(action.call()).thenReturn("Success");

        String result = retryService.executeWithRetry(action, "Test Action");
        assertEquals("Success", result);

        verify(action, times(1)).call();
    }

    @Test
    void testRetryOnFailure() throws Exception {
        Callable<String> action = mock(Callable.class);
        when(action.call()).thenThrow(new RuntimeException("Failure")).thenReturn("Success");

        String result = retryService.executeWithRetry(action, "Test Action");
        assertEquals("Success", result);

        verify(action, times(2)).call();
    }

    @Test
    void testExceedMaxRetries() throws Exception {
        Callable<String> action = mock(Callable.class);
        when(action.call()).thenThrow(new RuntimeException("Failure"));

        assertThrows(Exception.class, () -> retryService.executeWithRetry(action, "Test Action"));

        verify(action, times(3)).call();
    }
}
