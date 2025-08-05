package org.discogs.query.service;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.helpers.LogHelper;
import org.discogs.query.interfaces.BatchMarketplaceService;
import org.discogs.query.interfaces.DiscogsQueryService;
import org.discogs.query.interfaces.DiscogsWebScraperClient;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsRequestDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.discogs.query.model.enums.DiscogsFormats;
import org.discogs.query.service.discogs.DiscogsCollectionService;
import org.springframework.stereotype.Service;

/**
 * Service for processing Discogs queries using asynchronous tasks. This service handles query
 * processing, result filtering, and timeout management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueryProcessingService {

  private final DiscogsQueryService discogsQueryService;
  private final NormalizationService normalizationService;
  private final DiscogsWebScraperClient discogsWebScraperClient;
  private final DiscogsCollectionService discogsCollectionService;
  private final BatchMarketplaceService batchMarketplaceService;
  private final AsyncQueryService asyncQueryService;

  private List<DiscogsQueryDTO> generateQueriesBasedOnFormat(
      final @Valid DiscogsQueryDTO discogsQueryDTO) {
    return List.of(
        generateQueryForFormat(discogsQueryDTO, DiscogsFormats.LP.getFormat()),
        generateQueryForFormat(discogsQueryDTO, DiscogsFormats.VINYL_COMPILATION.getFormat()),
        generateQueryForFormat(discogsQueryDTO, DiscogsFormats.VINYL.getFormat()));
  }

  private DiscogsQueryDTO generateQueryForFormat(
      final DiscogsQueryDTO discogsQueryDTO, final String format) {
    return new DiscogsQueryDTO(
        discogsQueryDTO.artist(),
        discogsQueryDTO.album(),
        discogsQueryDTO.track(),
        discogsQueryDTO.title(),
        format,
        discogsQueryDTO.country(),
        discogsQueryDTO.types(),
        discogsQueryDTO.barcode());
  }

  /**
   * Processes each Discogs query asynchronously, normalizes the queries, and retrieves the results
   * from the Discogs API.
   *
   * @param discogsRequestDTO the {@link DiscogsRequestDTO} objects to process
   * @param timeoutInSeconds the timeout in seconds for each query to be processed
   * @return a list of {@link DiscogsResultDTO} objects containing the query results
   */
  public List<DiscogsResultDTO> processQueries(
      final DiscogsRequestDTO discogsRequestDTO, final long timeoutInSeconds) {
    // Process each original query in parallel and collect unique results
    List<DiscogsResultDTO> discogsResultDTOS =
        discogsRequestDTO.queries().parallelStream()
            .map(
                originalQuery -> {
                  List<DiscogsQueryDTO> expandedQueries =
                      checkFormatOfQueryAndGenerateList(originalQuery);
                  List<DiscogsQueryDTO> normalizedQueries =
                      expandedQueries.stream().map(normalizationService::normalizeQuery).toList();
                  List<CompletableFuture<DiscogsResultDTO>> futures =
                      asyncQueryService.createOptimizedFutures(normalizedQueries);
                  List<DiscogsResultDTO> combinedResults =
                      handleFuturesWithTimeout(futures, timeoutInSeconds);
                  Set<DiscogsEntryDTO> uniqueResults =
                      combinedResults.stream()
                          .flatMap(result -> result.results().stream())
                          .collect(Collectors.toSet());
                  return new DiscogsResultDTO(originalQuery, new ArrayList<>(uniqueResults));
                })
            .toList();

    if (discogsRequestDTO.username() != null && !discogsRequestDTO.username().isBlank()) {
      return discogsCollectionService.filterOwnedReleases(
          discogsRequestDTO.username(), discogsResultDTOS);
    }
    return discogsResultDTOS;
  }

  private List<DiscogsQueryDTO> checkFormatOfQueryAndGenerateList(
      final DiscogsQueryDTO discogsQueryDTO) {
    return DiscogsFormats.ALL_VINYLS.getFormat().equalsIgnoreCase(discogsQueryDTO.format())
        ? generateQueriesBasedOnFormat(discogsQueryDTO)
        : Collections.singletonList(discogsQueryDTO);
  }

  /**
   * Filters out Discogs entries that are not shipping from the UK marketplace. Now uses batch
   * processing to eliminate N+1 API calls.
   *
   * @param results the list of {@link DiscogsResultDTO} objects containing search results
   * @return a filtered list of {@link DiscogsResultDTO} objects where only UK-shipping entries
   *     remain
   */
  public List<DiscogsResultDTO> filterOutEntriesNotShippingFromUk(
      final List<DiscogsResultDTO> results) {
    return results.stream()
        .map(
            discogsResultDTO -> {
              // Use batch marketplace service to optimize API calls
              List<DiscogsEntryDTO> filteredEntries =
                  batchMarketplaceService.filterEntriesWithUKMarketplace(
                      discogsResultDTO.results());
              return new DiscogsResultDTO(discogsResultDTO.searchQuery(), filteredEntries);
            })
        .filter(discogsResultDTO -> !discogsResultDTO.results().isEmpty())
        .toList();
  }

  /**
   * Handles the completion of asynchronous tasks, enforcing a timeout for each query. If a query
   * times out, the task is canceled.
   *
   * @param futures the list of {@link CompletableFuture} objects to process
   * @param timeoutInSeconds the timeout in seconds for each query
   * @return a list of {@link DiscogsResultDTO} objects, or an empty list if none are completed
   *     within the timeout
   */
  private List<DiscogsResultDTO> handleFuturesWithTimeout(
      final List<CompletableFuture<DiscogsResultDTO>> futures, final long timeoutInSeconds) {
    return futures.stream()
        .map(future -> getFutureResultWithTimeout(future, timeoutInSeconds))
        .filter(Objects::nonNull)
        .peek(result -> LogHelper.debug(() -> "Received result: {}", result))
        .toList();
  }

  /**
   * Retrieves the result of a {@link CompletableFuture} with a specified timeout. If the future
   * does not complete within the timeout, the task is canceled.
   *
   * @param future the {@link CompletableFuture} to retrieve the result from
   * @param timeoutInSeconds the timeout in seconds
   * @return the {@link DiscogsResultDTO} result, or {@code null} if the task was canceled or failed
   */
  private DiscogsResultDTO getFutureResultWithTimeout(
      final CompletableFuture<DiscogsResultDTO> future, final long timeoutInSeconds) {
    try {
      return future.get(timeoutInSeconds, TimeUnit.SECONDS);
    } catch (final InterruptedException | ExecutionException e) {
      LogHelper.error(() -> "Error processing query", e);
      return null;
    } catch (final TimeoutException e) {
      LogHelper.warn(() -> "Query processing timed out");
      future.cancel(true);
      return null;
    }
  }
}
