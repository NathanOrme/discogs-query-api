package org.discogs.query.service.discogs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.DiscogsEntry;
import org.discogs.query.domain.DiscogsMarketplaceResult;
import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.exceptions.DiscogsSearchException;
import org.discogs.query.helpers.DiscogsUrlBuilder;
import org.discogs.query.helpers.StringHelper;
import org.discogs.query.interfaces.DiscogsAPIClient;
import org.discogs.query.interfaces.DiscogsFilterService;
import org.discogs.query.interfaces.DiscogsQueryService;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.discogs.query.model.enums.DiscogsFormats;
import org.discogs.query.service.utils.CompletableFutureService;
import org.discogs.query.service.utils.MappingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of {@link DiscogsQueryService} that interacts with the
 * Discogs API. This service handles search requests and processes the API responses.
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
    private final CompletableFutureService completableFutureService;

    /**
     * Sets the lowest price and number for sale for a Discogs entry based on the marketplace result.
     *
     * @param entry                    the Discogs entry to update
     * @param discogsMarketplaceResult the marketplace result containing pricing information
     */
    private static void setLowestPriceResultAndNumberForSale(final DiscogsEntry entry,
                                                             final DiscogsMarketplaceResult discogsMarketplaceResult) {
        entry.setNumberForSale(discogsMarketplaceResult.getNumberForSale());
        var lowestPriceResult = discogsMarketplaceResult.getResult();
        if (lowestPriceResult != null) {
            entry.setLowestPrice(lowestPriceResult.getValue());
            log.debug("Amended lowest price for entry: {}", entry);
        }
    }

    /**
     * Checks if the given format is a compilation format.
     *
     * @param discogsQueryDTO the DTO containing the query parameters
     * @return {@code true} if the format is a compilation format, {@code false} otherwise
     */
    static boolean isCompilationFormat(final DiscogsQueryDTO discogsQueryDTO) {
        String format = discogsQueryDTO.format();
        String compFormat = DiscogsFormats.COMP.getFormat();
        String vinylCompFormat = DiscogsFormats.VINYL_COMPILATION.getFormat();
        return format != null && (format.equals(compFormat) || format.equals(vinylCompFormat));
    }

    /**
     * Searches for results based on the given query.
     *
     * @param discogsQueryDTO the DTO containing the search query parameters
     * @return a {@link DiscogsResultDTO} containing search results or empty if an error occurs
     */
    @Override
    public DiscogsResultDTO searchBasedOnQuery(final DiscogsQueryDTO discogsQueryDTO) {
        try {
            log.info("Starting search for query: {}", discogsQueryDTO);

            String searchUrl = discogsUrlBuilder.buildSearchUrl(discogsQueryDTO);
            log.debug("Built search URL: {}", searchUrl);

            DiscogsResult results = performSearch(searchUrl);
            log.info("Received {} results from Discogs API", results.getResults().size());

            if (stringHelper.isNotNullOrBlank(discogsQueryDTO.barcode())) {
                return mappingService.mapObjectToDTO(results, discogsQueryDTO);
            }

            if (isCompilationFormat(discogsQueryDTO) && !stringHelper.isNotNullOrBlank(discogsQueryDTO.album())) {
                log.info("Processing compilation search...");
                processCompilationSearch(discogsQueryDTO, results);
                log.info("Total results after processing compilation search: {}", results.getResults().size());
            }

            correctUriForResultEntries(results);
            log.debug("URIs for result entries corrected");

            filterAndSortResults(discogsQueryDTO, results);

            getLowestPriceOnMarketplace(results);
            discogsFilterService.filterOutEmptyLowestPrice(results);

            DiscogsResultDTO resultDTO = mappingService.mapObjectToDTO(results, discogsQueryDTO);
            log.info("Search processing completed successfully for query: {}", discogsQueryDTO);

            return resultDTO;
        } catch (final DiscogsSearchException e) {
            log.error("DiscogsSearchException while processing query: {}. Error: {}",
                    discogsQueryDTO, e.getMessage(), e);
            return new DiscogsResultDTO(null, List.of()); // Return an empty DTO with an empty list
        } catch (final Exception e) {
            log.error(UNEXPECTED_ISSUE_OCCURRED + " while processing query: {}. Error: {}",
                    discogsQueryDTO, e.getMessage(), e);
            throw new DiscogsSearchException(UNEXPECTED_ISSUE_OCCURRED, e);
        }
    }

    /**
     * Performs a search using the given URL.
     *
     * @param searchUrl the URL to use for the search request
     * @return a {@link DiscogsResult} containing the search results
     */
    private DiscogsResult performSearch(final String searchUrl) {
        log.info("Sending search request to Discogs API...");
        return discogsAPIClient.getResultsForQuery(searchUrl);
    }

    /**
     * Filters and sorts the search results based on the given query parameters.
     *
     * @param discogsQueryDTO the DTO containing the search query parameters
     * @param results         the search results to filter and sort
     */
    private void filterAndSortResults(final DiscogsQueryDTO discogsQueryDTO, final DiscogsResult results) {
        log.info("Filtering and sorting results");
        discogsFilterService.filterAndSortResults(discogsQueryDTO, results);
        log.info("Filtering and sorting completed");
    }

    /**
     * Processes the compilation search and merges results with the initial search results.
     *
     * @param discogsQueryDTO the DTO containing the query parameters
     * @param results         the initial search results to merge with
     */
    private void processCompilationSearch(final DiscogsQueryDTO discogsQueryDTO, final DiscogsResult results) {
        log.debug("Generating compilation search URL for query: {}", discogsQueryDTO);
        String searchUrl = discogsUrlBuilder.generateCompilationSearchUrl(discogsQueryDTO);
        log.debug("Compilation search URL: {}", searchUrl);

        DiscogsResult compResults = discogsAPIClient.getResultsForQuery(searchUrl);
        log.info("Received {} compilation results from Discogs API", compResults.getResults().size());

        List<DiscogsEntry> mergedResults = Stream.concat(results.getResults().stream(),
                        compResults.getResults().stream())
                .distinct()
                .toList();

        results.setResults(mergedResults);
        log.debug("Merged results with compilation results. Total results: {}", results.getResults().size());
    }

    /**
     * Corrects the URIs for the result entries if they don't include the base URL.
     *
     * @param results the search results containing entries with potentially incorrect URIs
     */
    private void correctUriForResultEntries(final DiscogsResult results) {
        log.debug("Correcting URIs for result entries");
        results.getResults().parallelStream()
                .filter(entry -> !entry.getUri().contains(discogsUrlBuilder.getDiscogsWebsiteBaseUrl()))
                .forEach(entry -> entry.setUri(buildCorrectUri(entry)));
        log.debug("URI correction completed");
    }

    /**
     * Builds the correct URI for a Discogs entry.
     *
     * @param entry the Discogs entry to build the URI for
     * @return the corrected URI
     */
    private String buildCorrectUri(final DiscogsEntry entry) {
        return discogsUrlBuilder.getDiscogsWebsiteBaseUrl().concat(entry.getUri());
    }

    /**
     * Retrieves the lowest price for each result entry from the marketplace.
     *
     * @param results the search results containing entries to query for prices
     */
    private void getLowestPriceOnMarketplace(final DiscogsResult results) {
        if (results == null || results.getResults().isEmpty()) {
            log.warn("No results found in DiscogsResult.");
            return;
        }

        List<CompletableFuture<Void>> futures = results.getResults().parallelStream()
                .map(entry -> CompletableFuture.runAsync(() -> {
                    try {
                        log.debug("Generating marketplace URL for entry: {}", entry);
                        String marketplaceUrl = discogsUrlBuilder.buildMarketplaceUrl(entry);
                        log.debug("Getting marketplace result for entry: {}", entry);
                        DiscogsMarketplaceResult discogsMarketplaceResult =
                                discogsAPIClient.getMarketplaceResultForQuery(marketplaceUrl);

                        if (discogsMarketplaceResult != null) {
                            setLowestPriceResultAndNumberForSale(entry, discogsMarketplaceResult);
                        } else {
                            log.warn("Marketplace result is null for entry: {}", entry);
                        }
                    } catch (final Exception e) {
                        log.error("Failed to process entry: {} due to {}", entry, e.getMessage(), e);
                    }
                }))
                .collect(Collectors.toList());

        completableFutureService.processFuturesWithTimeout(futures);
    }
}
