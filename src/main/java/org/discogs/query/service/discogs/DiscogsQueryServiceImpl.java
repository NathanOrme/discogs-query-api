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
import org.discogs.query.service.ResultMappingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

/**
 * Implementation of {@link DiscogsQueryService} that interacts with the
 * Discogs API.
 * This service handles search requests and processes the API responses.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiscogsQueryServiceImpl implements DiscogsQueryService {

    private static final String UNEXPECTED_ISSUE_OCCURRED = "Unexpected issue occurred";

    private final DiscogsAPIClient discogsAPIClient;
    private final ResultMappingService resultMappingService;
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
        String format = discogsQueryDTO.getFormat();
        String compFormat = DiscogsFormats.COMP.getFormat();
        String vinylCompFormat = DiscogsFormats.VINYL_COMPILATION.getFormat();
        boolean isCompilation = compFormat.equalsIgnoreCase(format) || vinylCompFormat.equalsIgnoreCase(format);
        log.debug("Checking if format is compilation: {}. Result: {}", format, isCompilation);
        return isCompilation;
    }

    /**
     * Searches the Discogs database based on the provided query.
     *
     * @param discogsQueryDTO the search query containing artist, track, and optional format information
     * @return a {@link DiscogsResultDTO} with the search results
     */
    @Override
    public DiscogsResultDTO searchBasedOnQuery(final DiscogsQueryDTO discogsQueryDTO) {
        try {
            log.info("Starting search for query: {}", discogsQueryDTO);

            String searchUrl = discogsUrlBuilder.buildSearchUrl(discogsQueryDTO);
            log.debug("Built search URL: {}", searchUrl);

            DiscogsResult results = performSearch(searchUrl);
            log.info("Received {} results from Discogs API", results.getResults().size());

            if (stringHelper.isNotNullOrBlank(discogsQueryDTO.getBarcode())) {
                return resultMappingService.mapObjectToDTO(results, discogsQueryDTO);
            }

            if (isCompilationFormat(discogsQueryDTO) && !stringHelper.isNotNullOrBlank(discogsQueryDTO.getAlbum())) {
                log.info("Processing compilation search...");
                processCompilationSearch(discogsQueryDTO, results);
                log.info("Total results after processing compilation search: {}", results.getResults().size());
            }

            correctUriForResultEntries(results);
            log.debug("URIs for result entries corrected");

            filterAndSortResults(discogsQueryDTO, results);

            getLowestPriceOnMarketplace(results);
            discogsFilterService.filterOutEmptyLowestPrice(results);

            DiscogsResultDTO resultDTO = resultMappingService.mapObjectToDTO(results, discogsQueryDTO);
            log.info("Search processing completed successfully for query: {}", discogsQueryDTO);

            return resultDTO;
        } catch (final DiscogsSearchException e) {
            log.error("DiscogsSearchException while processing query: {}. Error: {}", discogsQueryDTO, e.getMessage()
                    , e);
            return new DiscogsResultDTO(); // Return an empty DTO or handle as per your error strategy
        } catch (final Exception e) {
            log.error(UNEXPECTED_ISSUE_OCCURRED + " while processing query: {}. Error: {}", discogsQueryDTO,
                    e.getMessage(), e);
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
        log.info("Sending search request to Discogs API...");
        return discogsAPIClient.getResultsForQuery(searchUrl);
    }

    /**
     * Filters and sorts the search results based on the query.
     *
     * @param discogsQueryDTO the search query
     * @param results         the search results to be filtered and sorted
     */
    private void filterAndSortResults(final DiscogsQueryDTO discogsQueryDTO, final DiscogsResult results) {
        log.info("Filtering and sorting results");
        discogsFilterService.filterAndSortResults(discogsQueryDTO, results);
        log.info("Filtering and sorting completed");
    }

    /**
     * Processes compilation search results by merging them with the original results.
     *
     * @param discogsQueryDTO the search query
     * @param results         the original search results
     */
    private void processCompilationSearch(final DiscogsQueryDTO discogsQueryDTO, final DiscogsResult results) {
        log.debug("Generating compilation search URL for query: {}", discogsQueryDTO);
        String searchUrl = discogsUrlBuilder.generateCompilationSearchUrl(discogsQueryDTO);
        log.debug("Compilation search URL: {}", searchUrl);

        DiscogsResult compResults = discogsAPIClient.getResultsForQuery(searchUrl);
        log.info("Received {} compilation results from Discogs API", compResults.getResults().size());

        List<DiscogsEntry> mergedResults = Stream.concat(
                        results.getResults().stream(),
                        compResults.getResults().stream()
                )
                .distinct()
                .toList();

        results.setResults(mergedResults);


        log.debug("Merged results with compilation results. Total results: {}", results.getResults().size());
    }

    /**
     * Corrects the URIs for result entries to ensure they are fully qualified.
     *
     * @param results the search results containing entries with URIs
     */
    private void correctUriForResultEntries(final DiscogsResult results) {
        log.debug("Correcting URIs for result entries");
        results.getResults().parallelStream()
                .filter(entry -> !entry.getUri().contains(discogsUrlBuilder.getDiscogsWebsiteBaseUrl()))
                .forEach(entry -> entry.setUri(buildCorrectUri(entry)));
        log.debug("URI correction completed");
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
            log.warn("No results found in DiscogsResult.");
            return;
        }

        results.getResults().parallelStream().forEach(entry -> {
            try {
                log.debug("Generating marketplace URL for entry: {}", entry);
                String marketplaceUrl = discogsUrlBuilder.builldMarketplaceUrl(entry);
                log.debug("Getting marketplace result for entry: {}", entry);
                var discogsMarketplaceResult = discogsAPIClient.getMarketplaceResultForQuery(marketplaceUrl);

                if (discogsMarketplaceResult != null) {
                    setLowestPriceResultAndNumberForSale(entry, discogsMarketplaceResult);
                } else {
                    log.warn("Marketplace result is null for entry: {}", entry);
                }
            } catch (final Exception e) {
                log.error("Failed to process entry: {} due to {}", entry, e.getMessage(), e);
            }
        });
    }

    /**
     * Updates the given {@link DiscogsEntry} with the lowest price and number for sale
     * from the provided {@link DiscogsMarketplaceResult}.
     *
     * <p>This method sets the number of items for sale and the lowest price from the
     * {@code DiscogsMarketplaceResult} to the corresponding fields in the {@code DiscogsEntry}.
     * It also logs a debug message if a lowest price is set.</p>
     *
     * @param entry                    the {@link DiscogsEntry} object to be updated. It should be an instance
     *                                 where the number of items for sale and the lowest price need to be set.
     * @param discogsMarketplaceResult the {@link DiscogsMarketplaceResult} object containing
     *                                 the number of items for sale and the lowest price result.
     *                                 It should not be {@code null}. The lowest price is obtained
     *                                 from the result inside this object.
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

}
