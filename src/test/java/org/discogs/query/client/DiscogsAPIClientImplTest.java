package org.discogs.query.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.Callable;
import org.discogs.query.domain.api.DiscogsResult;
import org.discogs.query.exceptions.DiscogsSearchException;
import org.discogs.query.interfaces.CircuitBreakerService;
import org.discogs.query.interfaces.HttpRequestService;
import org.discogs.query.interfaces.RateLimiterService;
import org.discogs.query.interfaces.RetryService;
import org.discogs.query.service.requests.CircuitBreakerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DiscogsAPIClientImplTest {

  @Mock private HttpRequestService httpRequestService;

  @Mock private RateLimiterService rateLimiterService;

  @Mock private RetryService retryService;

  @Mock private CircuitBreakerService circuitBreakerService;

  @InjectMocks private DiscogsAPIClientImpl client;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetResultsForQuerySuccess() throws Exception {
    String searchUrl = "http://example.com/search";
    DiscogsResult expectedResult = new DiscogsResult();

    when(circuitBreakerService.execute(any(CircuitBreakerService.OperationWithException.class)))
        .thenAnswer(
            invocation -> {
              CircuitBreakerService.OperationWithException<?> operation = invocation.getArgument(0);
              return operation.execute();
            });
    when(retryService.executeWithRetry(any(Callable.class), eq("Discogs Search API Request")))
        .thenReturn(expectedResult);

    DiscogsResult result = client.getResultsForQuery(searchUrl);

    verify(circuitBreakerService).execute(any(CircuitBreakerService.OperationWithException.class));
    verify(rateLimiterService).waitForRateLimit();
    verify(retryService).executeWithRetry(any(Callable.class), eq("Discogs Search API Request"));
    assertSame(expectedResult, result, "The result should match the expected result.");
  }

  @Test
  void testGetResultsForQueryRetryFailure() throws Exception {
    String searchUrl = "http://example.com/search";

    when(circuitBreakerService.execute(any(CircuitBreakerService.OperationWithException.class)))
        .thenAnswer(
            invocation -> {
              CircuitBreakerService.OperationWithException<?> operation = invocation.getArgument(0);
              return operation.execute();
            });
    when(retryService.executeWithRetry(any(Callable.class), eq("Discogs Search API Request")))
        .thenThrow(new RuntimeException("Simulated error"));

    try {
      client.getResultsForQuery(searchUrl);
      fail("Expected DiscogsSearchException to be thrown.");
    } catch (final DiscogsSearchException e) {
      assertEquals("Failed to fetch data from Discogs API", e.getMessage());
    }

    verify(circuitBreakerService).execute(any(CircuitBreakerService.OperationWithException.class));
    verify(rateLimiterService).waitForRateLimit();
    verify(retryService).executeWithRetry(any(Callable.class), eq("Discogs Search API Request"));
  }
}
