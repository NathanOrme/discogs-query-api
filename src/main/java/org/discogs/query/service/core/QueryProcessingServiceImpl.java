package org.discogs.query.service.core;

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
import org.discogs.query.interfaces.DiscogsCollectionService;
import org.discogs.query.interfaces.DiscogsQueryService;
import org.discogs.query.interfaces.DiscogsWebScraperClient;
import org.discogs.query.interfaces.NormalizationService;
import org.discogs.query.interfaces.QueryProcessingService;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsRequestDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.discogs.query.model.enums.DiscogsFormats;
import org.springframework.stereotype.Service;

/**
 * Service for processing Discogs queries using asynchronous tasks. This service handles query
 * processing, result filtering, and timeout management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueryProcessingServiceImpl implements QueryProcessingService {

  private final DiscogsQueryService discogsQueryService;
  private final NormalizationService normalizationService;
  private final DiscogsWebScraperClient discogsWebScraperClient;
  private final DiscogsCollectionService discogsCollectionService;

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

  @Override
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
                      createFuturesForQueries(normalizedQueries);
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

  @Override
  public List<DiscogsResultDTO> filterOutEntriesNotShippingFromUk(
      final List<DiscogsResultDTO> results) {
    return results.stream()
        .map(
            discogsResultDTO -> {
              // Filter out DiscogsEntryDTOs that do not have UK marketplace listings
              List<DiscogsEntryDTO> filteredEntries =
                  discogsResultDTO.results().parallelStream()
                      .filter(this::isUKMarketplaceEntry)
                      .toList();
              return new DiscogsResultDTO(discogsResultDTO.searchQuery(), filteredEntries);
            })
        .filter(discogsResultDTO -> !discogsResultDTO.results().isEmpty())
        .toList();
  }

  /**
   * Checks if a {@link DiscogsEntryDTO} has marketplace listings in the UK.
   *
   * @param discogsEntryDTO the entry to check
   * @return {@code true} if the entry has UK marketplace listings, {@code false} otherwise
   */
  private boolean isUKMarketplaceEntry(final DiscogsEntryDTO discogsEntryDTO) {
    try {
      return !discogsWebScraperClient
          .getMarketplaceResultsForRelease(String.valueOf(discogsEntryDTO.id()))
          .isEmpty();
    } catch (final Exception e) {
      LogHelper.error(e::getMessage);
      return false;
    }
  }

  /**
   * Creates a list of asynchronous tasks (futures) for each query.
   *
   * @param discogsQueryDTOList the list of {@link DiscogsQueryDTO} objects to process
   * @return a list of {@link CompletableFuture} objects for each query
   */
  private List<CompletableFuture<DiscogsResultDTO>> createFuturesForQueries(
      final List<DiscogsQueryDTO> discogsQueryDTOList) {
    return discogsQueryDTOList.stream()
        .map(
            query ->
                CompletableFuture.supplyAsync(
                    () -> {
                      log.debug("Processing query: {}", query);
                      return discogsQueryService.searchBasedOnQuery(query);
                    }))
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