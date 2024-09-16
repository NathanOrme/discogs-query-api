package org.discogs.query.service;

import org.discogs.query.interfaces.DiscogsQueryService;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.discogs.query.service.utils.CompletableFutureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class QueryProcessingServiceTest {

    @Mock
    private DiscogsQueryService discogsQueryService;

    @Mock
    private CompletableFutureService completableFutureService;

    @InjectMocks
    private QueryProcessingService queryProcessingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processQueries_Success() {
        DiscogsQueryDTO queryDTO = new DiscogsQueryDTO(null, null, null, null,
                null, null, null);
        DiscogsResultDTO resultDTO = new DiscogsResultDTO(null, null);

        when(discogsQueryService.searchBasedOnQuery(any())).thenReturn(resultDTO);

        when(completableFutureService.processFuturesWithTimeout(any()))
                .thenReturn(Collections.singletonList(resultDTO));

        // Call the method being tested
        List<DiscogsResultDTO> results = queryProcessingService.processQueries(Collections.singletonList(queryDTO));

        // Verify the results
        assertEquals(1, results.size(), "Expected size to be 1");
        assertEquals(resultDTO, results.get(0), "Expected result to match the mocked resultDTO");
    }

    @Test
    void processQueries_EmptyResult() {
        DiscogsQueryDTO queryDTO = new DiscogsQueryDTO(null, null, null,
                null, null, null, null);

        // Create a CompletableFuture that completes with null to simulate no results
        CompletableFuture<DiscogsResultDTO> future = CompletableFuture.completedFuture(null);

        // Mock the DiscogsQueryService to return null
        when(discogsQueryService.searchBasedOnQuery(queryDTO)).thenReturn(null);

        // Mock the CompletableFutureService to return an empty list
        when(completableFutureService.processFuturesWithTimeout(Collections.singletonList(future)))
                .thenReturn(Collections.emptyList());

        // Call the method being tested
        List<DiscogsResultDTO> results = queryProcessingService.processQueries(Collections.singletonList(queryDTO));

        // Verify the results
        assertEquals(0, results.size(), "Expected size to be 0");
    }
}
