package org.discogs.query.service.requests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RetryServiceImpl.class})
@ExtendWith(SpringExtension.class)
class RetryServiceImplDiffblueTest {
    @Autowired
    private RetryServiceImpl retryServiceImpl;

    /**
     * Method under test:
     * {@link RetryServiceImpl#executeWithRetry(Callable, String)}
     */
    @Test
    void testExecuteWithRetry() throws Exception {
        // Arrange
        Callable<Object> action = mock(Callable.class);
        when(action.call()).thenReturn("Call");

        // Act
        Object actualExecuteWithRetryResult = retryServiceImpl.executeWithRetry(action, "Action Description");

        // Assert
        verify(action).call();
        assertEquals("Call", actualExecuteWithRetryResult);
    }

    /**
     * Method under test:
     * {@link RetryServiceImpl#executeWithRetry(Callable, String)}
     */
    @Test
    void testExecuteWithRetry2() throws Exception {
        // Arrange
        Callable<Object> action = mock(Callable.class);
        when(action.call()).thenThrow(new Exception("Attempting {}. Attempt {} of {}"));

        // Act and Assert
        assertThrows(Exception.class, () -> retryServiceImpl.executeWithRetry(action, "Action Description"));
        verify(action, atLeast(1)).call();
    }
}
