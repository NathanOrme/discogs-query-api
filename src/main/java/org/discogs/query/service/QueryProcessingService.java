package org.discogs.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.exceptions.NoMarketplaceListingsException;
import org.discogs.query.interfaces.DiscogsQueryService;
import org.discogs.query.interfaces.DiscogsWebScraperClient;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Service for processing Discogs queries using asynchronous tasks.
 * Handles query processing with timeout management and cancellation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueryProcessingService {

    private final DiscogsQueryService discogsQueryService;
    private final NormalizationService normalizationService;
    private final DiscogsWebScraperClient discogsWebScraperClient;

    /**
     * Processes each query asynchronously and retrieves the results from the Discogs API.
     *
     * @param discogsQueryDTOList the list of {@link DiscogsQueryDTO} objects to process
     * @param timeoutInSeconds    the timeout in seconds for each query
     * @return a list of {@link DiscogsResultDTO} objects
     */
    public List<DiscogsResultDTO> processQueries(final List<DiscogsQueryDTO> discogsQueryDTOList,
                                                 final long timeoutInSeconds) {
        List<DiscogsQueryDTO> normalizedList = discogsQueryDTOList.parallelStream()
                .map(normalizationService::normalizeQuery)
                .toList();
        List<CompletableFuture<DiscogsResultDTO>> futures = createFuturesForQueries(normalizedList);
        return handleFuturesWithTimeout(futures, timeoutInSeconds);
    }

    public List<DiscogsResultDTO> filterOutEntriesNotShippingFromUk(final List<DiscogsResultDTO> results) {
        return results.stream()
                .map(discogsResultDTO -> {
                    // Filter out DiscogsEntryDTOs that do not have shipping results
                    List<DiscogsEntryDTO> filteredEntries = discogsResultDTO.results().parallelStream()
                            .filter(this::isUKMarketplaceEntry)
                            .toList();

                    // Return a new DiscogsResultDTO with the filtered entries
                    return new DiscogsResultDTO(discogsResultDTO.searchQuery(), filteredEntries);
                })
                .filter(discogsResultDTO -> !discogsResultDTO.results().isEmpty()) // Filter out empty result DTOs
                .toList();
    }

    private boolean isUKMarketplaceEntry(final DiscogsEntryDTO discogsEntryDTO) {
        try {
            return !discogsWebScraperClient
                    .getMarketplaceResultsForRelease(String.valueOf(discogsEntryDTO.id()))
                    .isEmpty();
        } catch (final Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }


    private List<CompletableFuture<DiscogsResultDTO>> createFuturesForQueries(
            final List<DiscogsQueryDTO> discogsQueryDTOList) {
        return discogsQueryDTOList.stream()
                .map(query -> CompletableFuture.supplyAsync(() -> {
                    log.debug("Processing query: {}", query);
                    return discogsQueryService.searchBasedOnQuery(query);
                }))
                .toList();
    }

    private List<DiscogsResultDTO> handleFuturesWithTimeout(
            final List<CompletableFuture<DiscogsResultDTO>> futures,
            final long timeoutInSeconds) {
        return futures.stream()
                .map(future -> getFutureResultWithTimeout(future, timeoutInSeconds))
                .filter(Objects::nonNull)
                .peek(result -> log.debug("Received result: {}", result))
                .toList();
    }

    private DiscogsResultDTO getFutureResultWithTimeout(
            final CompletableFuture<DiscogsResultDTO> future, final long timeoutInSeconds) {
        try {
            return future.get(timeoutInSeconds, TimeUnit.SECONDS);
        } catch (final InterruptedException | ExecutionException e) {
            log.error("Error processing query", e);
            return null;
        } catch (final TimeoutException e) {
            log.warn("Query processing timed out");
            future.cancel(true); // Cancel the task if it times out
            return null;
        }
    }
}
