package org.discogs.query.service.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CompletableFutureServiceTest {

    private CompletableFutureService completableFutureService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        completableFutureService = new CompletableFutureService();
    }

    @Test
    void testProcessFuturesWithTimeout_Timeout() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000); // Simulate delay
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Result";
        });

        List<String> results = completableFutureService.processFuturesWithTimeout(List.of(future));

        assertTrue(results.isEmpty());
    }

    @Test
    void testProcessFuturesWithTimeout_Exception() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("Simulated exception");
        });

        List<String> results = completableFutureService.processFuturesWithTimeout(List.of(future));

        assertTrue(results.isEmpty());
    }
}
