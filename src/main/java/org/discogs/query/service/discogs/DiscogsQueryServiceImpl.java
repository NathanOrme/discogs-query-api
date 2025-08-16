package org.discogs.query.service.discogs;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.api.DiscogsEntry;
import org.discogs.query.domain.api.DiscogsMarketplaceResult;
import org.discogs.query.domain.api.DiscogsResult;
import org.discogs.query.exceptions.DiscogsSearchException;
import org.discogs.query.util.DiscogsUrlBuilder;
import org.discogs.query.helpers.LogHelper;
import org.discogs.query.util.StringHelper;
import org.discogs.query.interfaces.DiscogsAPIClient;
import org.discogs.query.interfaces.DiscogsFilterService;
import org.discogs.query.interfaces.DiscogsQueryService;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.discogs.query.model.enums.DiscogsFormats;
import org.discogs.query.interfaces.MappingService;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link DiscogsQueryService} that interacts with the Discogs API. This service
 * handles search requests and processes the API responses.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiscogsQueryServiceImpl implements DiscogsQueryService {

  private static final String UNEXPECTED_ISSUE_OCCURRED = "Unexpected issue occurred";
  private final DiscogsAPIClient discogsAPIClient;
  private final MappingService mappingService;
  private final DiscogsUrlBuilder discogsUrlBuilder;
  private final DiscogsFilterService discogsFilterService;
  private final StringHelper stringHelper;

  /**
   * Checks if the given {@link DiscogsQueryDTO} represents a compilation format.
   *
   * @param discogsQueryDTO the search query containing format information
   * @return true if the format is a compilation, false otherwise
   */
  static boolean isCompilationFormat(final DiscogsQueryDTO discogsQueryDTO) {
    String format = discogsQueryDTO.format();
    String compFormat = DiscogsFormats.COMP.getFormat();
    String vinylCompFormat = DiscogsFormats.VINYL_COMPILATION.getFormat();
    boolean isCompilation =
        compFormat.equalsIgnoreCase(format) || vinylCompFormat.equalsIgnoreCase(format);
    LogHelper.debug(
        () -> "Checking if format is compilation: {}. Result: {}", format, isCompilation);
    return isCompilation;
  }

  /**
   * Updates the given {@link DiscogsEntry} with the lowest price and number for sale from the
   * provided {@link DiscogsMarketplaceResult}.
   *
   * @param entry the DiscogsEntry to be updated.
   * @param discogsMarketplaceResult the marketplace result containing price info.
   */
  private static void setLowestPriceResultAndNumberForSale(
      final DiscogsEntry entry, final DiscogsMarketplaceResult discogsMarketplaceResult) {
    entry.setNumberForSale(discogsMarketplaceResult.getNumberForSale());
    var lowestPriceResult = discogsMarketplaceResult.getResult();
    if (lowestPriceResult != null) {
      entry.setLowestPrice(lowestPriceResult.getValue());
      LogHelper.debug(() -> "Amended lowest price for entry: {}", entry);
    }
  }

  private static Stream<DiscogsEntry> concatStreams(
      final DiscogsResult results, final DiscogsResult compResults) {
    return Stream.concat(results.getResults().stream(), compResults.getResults().stream())
        .distinct();
  }

  private static DiscogsEntry filterAndProcessEntry(
      final DiscogsEntry entry, final DiscogsMarketplaceResult discogsMarketplaceResult) {
    if (discogsMarketplaceResult != null) {
      setLowestPriceResultAndNumberForSale(entry, discogsMarketplaceResult);
      return entry;
    } else {
      LogHelper.warn(
          () -> "Entry {} does not ship from United Kingdom or marketplace result is null.", entry);
      return null;
    }
  }

  /**
   * Searches the Discogs database based on the provided query.
   *
   * @param discogsQueryDTO the search query containing artist, track, and optional format
   *     information
   * @return a {@link DiscogsResultDTO} with the search results
   */
  @Override
  public DiscogsResultDTO searchBasedOnQuery(final DiscogsQueryDTO discogsQueryDTO) {
    try {
      LogHelper.info(() -> "Starting search for query: {}", discogsQueryDTO);
      String searchUrl = discogsUrlBuilder.buildSearchUrl(discogsQueryDTO);
      LogHelper.debug(() -> "Built search URL: {}", searchUrl);
      DiscogsResult results = performSearch(searchUrl);
      LogHelper.info(() -> "Received {} results from Discogs API", results.getResults().size());

      if (stringHelper.isNotNullOrBlank(discogsQueryDTO.barcode())) {
        return mappingService.mapObjectToDTO(results, discogsQueryDTO);
      }

      if (isCompilationFormat(discogsQueryDTO)
          && !stringHelper.isNotNullOrBlank(discogsQueryDTO.album())) {
        LogHelper.info(() -> "Processing compilation search...");
        processCompilationSearch(discogsQueryDTO, results);
        LogHelper.info(
            () -> "Total results after processing compilation search: {}",
            results.getResults().size());
      }

      correctUriForResultEntries(results);
      LogHelper.debug(() -> "URIs for result entries corrected");
      filterAndSortResults(discogsQueryDTO, results);
      getLowestPriceOnMarketplace(results);
      discogsFilterService.filterOutEmptyLowestPrice(results);
      DiscogsResultDTO resultDTO = mappingService.mapObjectToDTO(results, discogsQueryDTO);
      LogHelper.info(
          () -> "Search processing completed successfully for query: {}", discogsQueryDTO);
      return resultDTO;
    } catch (final DiscogsSearchException e) {
      LogHelper.error(
          () -> "DiscogsSearchException while processing query: {}. Error: {}",
          discogsQueryDTO,
          e.getMessage(),
          e);
      return new DiscogsResultDTO(null, null);
    } catch (final Exception e) {
      LogHelper.error(
          () -> UNEXPECTED_ISSUE_OCCURRED + " while processing query: {}. Error: {}",
          discogsQueryDTO,
          e.getMessage(),
          e);
      throw new DiscogsSearchException(UNEXPECTED_ISSUE_OCCURRED, e);
    }
  }

  /**
   * Performs a search request to the Discogs API and retrieves results.
   *
   * @param searchUrl the URL for the search request
   * @return a {@link DiscogsResult} containing search results
   */
  private DiscogsResult performSearch(final String searchUrl) {
    LogHelper.info(() -> "Sending search request to Discogs API...");
    return discogsAPIClient.getResultsForQuery(searchUrl);
  }

  /**
   * Filters and sorts the search results based on the query.
   *
   * @param discogsQueryDTO the search query
   * @param results the search results to be filtered and sorted
   */
  private void filterAndSortResults(
      final DiscogsQueryDTO discogsQueryDTO, final DiscogsResult results) {
    LogHelper.info(() -> "Filtering and sorting results");
    discogsFilterService.filterAndSortResults(discogsQueryDTO, results);
    LogHelper.info(() -> "Filtering and sorting completed");
  }

  /**
   * Processes compilation search results by merging them with the original results.
   *
   * @param discogsQueryDTO the search query
   * @param results the original search results
   */
  private void processCompilationSearch(
      final DiscogsQueryDTO discogsQueryDTO, final DiscogsResult results) {
    LogHelper.debug(() -> "Generating compilation search URL for query: {}", discogsQueryDTO);
    String searchUrl = discogsUrlBuilder.generateCompilationSearchUrl(discogsQueryDTO);
    LogHelper.debug(() -> "Compilation search URL: {}", searchUrl);
    DiscogsResult compResults = discogsAPIClient.getResultsForQuery(searchUrl);
    LogHelper.info(
        () -> "Received {} compilation results from Discogs API", compResults.getResults().size());
    List<DiscogsEntry> mergedResults = concatStreams(results, compResults).toList();
    results.setResults(mergedResults);
    LogHelper.debug(
        () -> "Merged results with compilation results. Total results: {}",
        results.getResults().size());
  }

  /**
   * Corrects the URIs for result entries to ensure they are fully qualified.
   *
   * @param results the search results containing entries with URIs
   */
  private void correctUriForResultEntries(final DiscogsResult results) {
    LogHelper.debug(() -> "Correcting URIs for result entries");
    results.getResults().parallelStream()
        .filter(entry -> !entry.getUri().contains(discogsUrlBuilder.getDiscogsWebsiteBaseUrl()))
        .forEach(entry -> entry.setUri(buildCorrectUri(entry)));
    LogHelper.debug(() -> "URI correction completed");
  }

  /**
   * Builds the correct URI for a {@link DiscogsEntry} by prepending the base URL.
   *
   * @param entry the Discogs entry
   * @return the correct URI for the entry
   */
  private String buildCorrectUri(final DiscogsEntry entry) {
    return discogsUrlBuilder.getDiscogsWebsiteBaseUrl().concat(entry.getUri());
  }

  /**
   * Retrieves the lowest price for each entry from the marketplace and updates the entry.
   *
   * @param results the search results containing entries
   */
  private void getLowestPriceOnMarketplace(final DiscogsResult results) {
    if (results == null || results.getResults().isEmpty()) {
      LogHelper.warn(() -> "No results found in DiscogsResult.");
      return;
    }
    List<DiscogsEntry> filteredResults =
        results.getResults().parallelStream()
            .map(
                entry -> {
                  try {
                    var discogsMarketplaceResult = getDiscogsMarketplaceResult(entry);
                    return filterAndProcessEntry(entry, discogsMarketplaceResult);
                  } catch (final Exception e) {
                    LogHelper.error(
                        () -> "Failed to process entry: {} due to {}", entry, e.getMessage(), e);
                    return null;
                  }
                })
            .filter(Objects::nonNull)
            .toList();
    results.setResults(filteredResults);
  }

  private DiscogsMarketplaceResult getDiscogsMarketplaceResult(final DiscogsEntry entry) {
    LogHelper.debug(() -> "Generating marketplace URL for entry: {}", entry);
    String marketplaceUrl = discogsUrlBuilder.buildMarketplaceUrl(entry);
    LogHelper.debug(() -> "Getting marketplace result for entry: {}", entry);
    return discogsAPIClient.getMarketplaceResultForQuery(marketplaceUrl);
  }
}
