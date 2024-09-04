package org.discogs.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.DiscogsEntry;
import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.enums.DiscogsFormats;
import org.discogs.query.exceptions.DiscogsSearchException;
import org.discogs.query.helpers.DiscogsUrlBuilder;
import org.discogs.query.helpers.StringHelper;
import org.discogs.query.interfaces.DiscogsAPIClient;
import org.discogs.query.interfaces.DiscogsFilterService;
import org.discogs.query.interfaces.DiscogsQueryService;
import org.discogs.query.mapper.DiscogsResultMapper;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of the {@link DiscogsQueryService} interface for
 * interacting with the Discogs API.
 * This service handles search requests, processes the API responses, and
 * includes methods for
 * handling compilation searches and correcting URIs for results.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiscogsQueryServiceImpl implements DiscogsQueryService {

    /**
     * Error message used for unexpected issues.
     */
    private static final String UNEXPECTED_ISSUE_OCCURRED = "Unexpected issue" +
            " occurred";

    /**
     * API Client for Discogs.
     */
    private final DiscogsAPIClient discogsAPIClient;
    /**
     * Mapper to convert domain to DTO.
     */
    private final DiscogsResultMapper discogsResultMapper;
    /**
     * Builder for URLs for searches.
     */
    private final DiscogsUrlBuilder discogsUrlBuilder;
    /**
     * Service used for filtering and sorting.
     */
    private final DiscogsFilterService discogsFilterService;
    /**
     * String Helper object to help string-based functionality.
     */
    private final StringHelper stringHelper;

    /**
     * Checks if the format in the given {@link DiscogsQueryDTO} is a
     * compilation format.
     * <p>
     * A format is considered a compilation if it matches the predefined
     * compilation formats
     * such as "COMP" or "VINYL_COMPILATION".
     *
     * @param discogsQueryDTO the query DTO containing format information
     * @return {@code true} if the format is a compilation format, {@code
     * false} otherwise
     */
    static boolean isCompilationFormat(final DiscogsQueryDTO discogsQueryDTO) {
        boolean isCompilation = isCompilation(discogsQueryDTO);
        log.debug("Checking if format is compilation: {}. Result: {}",
                discogsQueryDTO.getFormat(), isCompilation);
        return isCompilation;
    }

    /**
     * Determines if the format specified in the {@link DiscogsQueryDTO} is a
     * compilation format.
     * <p>
     * This method compares the format against predefined compilation formats.
     *
     * @param discogsQueryDTO the query DTO containing format information
     * @return {@code true} if the format is a compilation format, {@code
     * false} otherwise
     */
    private static boolean isCompilation(final DiscogsQueryDTO discogsQueryDTO) {
        String format = discogsQueryDTO.getFormat();
        String comp = DiscogsFormats.COMP.getFormat();
        String vinylComp = DiscogsFormats.VINYL_COMPILATION.getFormat();
        return comp.equalsIgnoreCase(format) || vinylComp.equalsIgnoreCase(format);
    }

    /**
     * Searches the Discogs database based on the provided query.
     * <p>
     * This method constructs the search URL, sends the request to the
     * Discogs API, processes the
     * results including handling compilation searches if applicable, and
     * maps the results to a
     * {@link DiscogsResultDTO} object. It also handles exceptions and logs
     * relevant information.
     *
     * @param discogsQueryDTO the search query containing artist, track, and
     *                        optional format information
     * @return a {@link DiscogsResultDTO} with the search results
     */
    @Override
    public DiscogsResultDTO searchBasedOnQuery(final DiscogsQueryDTO discogsQueryDTO) {
        try {
            log.info("Starting search for query: {}", discogsQueryDTO);

            String searchUrl =
                    discogsUrlBuilder.buildSearchUrl(discogsQueryDTO);
            log.debug("Built search URL: {}", searchUrl);

            log.info("Sending search request to Discogs API...");
            DiscogsResult results =
                    discogsAPIClient.getResultsForQuery(searchUrl);
            log.info("Received {} results from Discogs API",
                    results.getResults().size());

            if (isCompilationAndAlbumNotSupplied(discogsQueryDTO)) {
                log.info("Query format is a compilation. Processing "
                        + "compilation search...");
                processCompilationSearch(discogsQueryDTO, results);
                log.info("After processing compilation search, {} total "
                        + "results available", results.getResults().size());
            }

            correctUriForResultEntries(results);
            log.debug("Corrected URIs for result entries");

            log.info("Filtering and sorting results");
            discogsFilterService.filterAndSortResults(discogsQueryDTO, results);
            log.info("Filtering and sorting completed");

            getLowestPriceOnMarketplace(results);

            DiscogsResultDTO resultDTO =
                    discogsResultMapper.mapObjectToDTO(results,
                            discogsQueryDTO);
            log.info("Search processing completed successfully for query: {}"
                    , discogsQueryDTO);

            return resultDTO;
        } catch (final DiscogsSearchException e) {
            log.error("DiscogsSearchException occurred while processing "
                            + "query: {}. Error: {}",
                    discogsQueryDTO, e.getMessage(), e);
            return new DiscogsResultDTO(); // Return an empty DTO or handle
            // as per your error strategy
        } catch (final Exception e) {
            log.error(UNEXPECTED_ISSUE_OCCURRED + " while processing query: " +
                    "{}. Error: {}", discogsQueryDTO, e.getMessage(), e);
            throw new DiscogsSearchException(UNEXPECTED_ISSUE_OCCURRED, e);
        }
    }

    private boolean isCompilationAndAlbumNotSupplied(final DiscogsQueryDTO discogsQueryDTO) {
        return isCompilationFormat(discogsQueryDTO) &&
                !stringHelper.isNotNullOrBlank(discogsQueryDTO.getAlbum());
    }

    /**
     * Retrieves the lowest price for each entry in the results from the
     * Discogs Marketplace.
     * <p>
     * This method generates marketplace URLs for each entry, retrieves
     * marketplace results, and updates
     * the entries with the number for sale and lowest price information.
     *
     * @param results the {@link DiscogsResult} containing the search results
     */
    private void getLowestPriceOnMarketplace(final DiscogsResult results) {
        if (results == null || results.getResults().isEmpty()) {
            log.warn("No results found in DiscogsResult.");
            return;
        }

        results.getResults().forEach(entry -> {
            try {
                log.info("Generating marketplace URL for query: {}", entry);
                var marketplaceUrl =
                        discogsUrlBuilder.buildMarketplaceUrl(entry);
                log.info("Getting marketplace result for the following entry:"
                        + " {}", entry);
                var discogsMarketplaceResult =
                        discogsAPIClient.getMarketplaceResultForQuery(marketplaceUrl);
                if (discogsMarketplaceResult != null) {
                    entry.setNumberForSale(discogsMarketplaceResult.getNumberForSale());
                    var lowestPriceResult =
                            discogsMarketplaceResult.getResult();
                    if (lowestPriceResult != null) {
                        entry.setLowestPrice(lowestPriceResult.getValue());
                        log.info("Amended lowest price for the following "
                                + "entry: {}", entry);
                    }
                } else {
                    log.warn("Marketplace result is null for entry: {}", entry);
                }
            } catch (final Exception e) {
                log.error("Failed to process entry: {} due to {}", entry,
                        e.getMessage(), e);
            }
        });
    }

    /**
     * Corrects the URIs for result entries to ensure they are complete.
     * <p>
     * This method checks each entry's URI and updates it to include the base
     * URL if necessary.
     *
     * @param results the {@link DiscogsResult} containing the search results
     */
    private void correctUriForResultEntries(final DiscogsResult results) {
        log.debug("Correcting URIs for result entries");
        results.getResults().stream()
                .filter(entry -> !isContainingBaseUrl(entry))
                .forEach(entry -> entry.setUri(buildCorrectUri(entry)));
        log.debug("URI correction completed");
    }

    /**
     * Checks if the URI of the given entry contains the base URL.
     * <p>
     * This method is used to determine if the URI is already complete or if
     * it needs to be updated.
     *
     * @param entry the {@link DiscogsEntry} to check
     * @return {@code true} if the URI contains the base URL, {@code false}
     * otherwise
     */
    private boolean isContainingBaseUrl(final DiscogsEntry entry) {
        return entry.getUri().contains(discogsUrlBuilder.getDiscogsWebsiteBaseUrl());
    }

    /**
     * Builds a complete URI for the given entry by concatenating the base
     * URL with the entry's URI.
     * <p>
     * This method is used to ensure that all result entries have complete URIs.
     *
     * @param entry the {@link DiscogsEntry} for which to build the complete URI
     * @return the complete URI as a {@link String}
     */
    private String buildCorrectUri(final DiscogsEntry entry) {
        return discogsUrlBuilder.getDiscogsWebsiteBaseUrl()
                .concat(entry.getUri());
    }

    /**
     * Processes a compilation search by combining the results of a
     * compilation search with the original results.
     * <p>
     * This method generates a new search URL for compilations, retrieves the
     * compilation results, and merges
     * them with the original results.
     *
     * @param discogsQueryDTO the query DTO containing search parameters
     * @param results         the original {@link DiscogsResult} containing
     *                        search results
     */
    private void processCompilationSearch(final DiscogsQueryDTO discogsQueryDTO,
                                          final DiscogsResult results) {
        log.debug("Generating compilation search URL for query: {}",
                discogsQueryDTO);
        String searchUrl =
                discogsUrlBuilder.generateCompilationSearchUrl(discogsQueryDTO);
        log.debug("Compilation search URL: {}", searchUrl);

        log.info("Sending compilation search request to Discogs API...");
        DiscogsResult compResults =
                discogsAPIClient.getResultsForQuery(searchUrl);
        log.info("Received {} compilation results from Discogs API",
                compResults.getResults().size());

        Set<DiscogsEntry> entries = new HashSet<>(results.getResults());
        entries.addAll(compResults.getResults());
        results.setResults(new ArrayList<>(entries));

        log.debug("Merged original results with compilation results. Total "
                + "results: {}", results.getResults().size());
    }
}
