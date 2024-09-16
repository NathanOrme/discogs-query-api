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
    private final MappingService mappingService;
    private final DiscogsUrlBuilder discogsUrlBuilder;
    private final DiscogsFilterService discogsFilterService;
    private final StringHelper stringHelper;
    private final CompletableFutureService completableFutureService;

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
            log.error("DiscogsSearchException while processing query: {}. Error: {}", discogsQueryDTO, e.getMessage()
                    , e);
            return new DiscogsResultDTO(null, null); // Return an empty DTO or handle as per your error strategy
        } catch (final Exception e) {
            log.error(UNEXPECTED_ISSUE_OCCURRED + " while processing query: {}. Error: {}", discogsQueryDTO,
                    e.getMessage(), e);
            throw new DiscogsSearchException(UNEXPECTED_ISSUE_OCCURRED, e);
        }
    }

    private DiscogsResult performSearch(final String searchUrl) {
        log.info("Sending search request to Discogs API...");
        return discogsAPIClient.getResultsForQuery(searchUrl);
    }

    private void filterAndSortResults(final DiscogsQueryDTO discogsQueryDTO, final DiscogsResult results) {
        log.info("Filtering and sorting results");
        discogsFilterService.filterAndSortResults(discogsQueryDTO, results);
        log.info("Filtering and sorting completed");
    }

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

    private void correctUriForResultEntries(final DiscogsResult results) {
        log.debug("Correcting URIs for result entries");
        results.getResults().parallelStream()
                .filter(entry -> !entry.getUri().contains(discogsUrlBuilder.getDiscogsWebsiteBaseUrl()))
                .forEach(entry -> entry.setUri(buildCorrectUri(entry)));
        log.debug("URI correction completed");
    }

    private String buildCorrectUri(final DiscogsEntry entry) {
        return discogsUrlBuilder.getDiscogsWebsiteBaseUrl().concat(entry.getUri());
    }

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
                        var discogsMarketplaceResult = discogsAPIClient.getMarketplaceResultForQuery(marketplaceUrl);

                        if (discogsMarketplaceResult != null) {
                            setLowestPriceResultAndNumberForSale(entry, discogsMarketplaceResult);
                        } else {
                            log.warn("Marketplace result is null for entry: {}", entry);
                        }
                    } catch (final Exception e) {
                        log.error("Failed to process entry: {} due to {}", entry, e.getMessage(), e);
                    }
                }))
                .toList();

        completableFutureService.processFuturesWithTimeout(futures.stream()
                .map(future -> future)
                .toList());
    }

    private static void setLowestPriceResultAndNumberForSale(final DiscogsEntry entry,
                                                             final DiscogsMarketplaceResult discogsMarketplaceResult) {
        entry.setNumberForSale(discogsMarketplaceResult.getNumberForSale());
        var lowestPriceResult = discogsMarketplaceResult.getResult();
        if (lowestPriceResult != null) {
            entry.setLowestPrice(lowestPriceResult.getValue());
            log.debug("Amended lowest price for entry: {}", entry);
        }
    }

    static boolean isCompilationFormat(final DiscogsQueryDTO discogsQueryDTO) {
        String format = discogsQueryDTO.format();
        String compFormat = DiscogsFormats.COMP.getFormat();
        String vinylCompFormat = DiscogsFormats.VINYL_COMPILATION.getFormat();
        boolean isCompilation = compFormat.equalsIgnoreCase(format) || vinylCompFormat.equalsIgnoreCase(format);
        log.debug("Checking if format is compilation: {}. Result: {}", format, isCompilation);
        return isCompilation;
    }
}
