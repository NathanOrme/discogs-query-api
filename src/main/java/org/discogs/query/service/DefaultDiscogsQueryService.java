package org.discogs.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.client.DiscogsAPIClient;
import org.discogs.query.domain.DiscogsEntry;
import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.enums.DiscogsFormats;
import org.discogs.query.exceptions.DiscogsSearchException;
import org.discogs.query.helpers.DiscogsUrlBuilder;
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
 * Implementation of {@link DiscogsQueryService} that interacts with the Discogs API.
 * This service handles search requests and processes the API responses.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultDiscogsQueryService implements DiscogsQueryService {

    private static final String UNEXPECTED_ISSUE_OCCURRED = "Unexpected issue occurred";

    private final DiscogsAPIClient discogsAPIClient;
    private final DiscogsResultMapper discogsResultMapper;
    private final DiscogsUrlBuilder discogsUrlBuilder;
    private final DiscogsFilterService discogsFilterService;

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

            log.info("Sending search request to Discogs API...");
            discogsAPIClient.getStringResultForQuery(searchUrl);
            DiscogsResult results = discogsAPIClient.getResultsForQuery(searchUrl);
            log.info("Received {} results from Discogs API", results.getResults().size());

            if (isCompilationFormat(discogsQueryDTO)) {
                log.info("Query format is a compilation. Processing compilation search...");
                processCompilationSearch(discogsQueryDTO, results);
                log.info("After processing compilation search, {} total results available",
                        results.getResults().size());
            }

            correctUriForResultEntries(results);
            log.debug("Corrected URIs for result entries");

            if (discogsQueryDTO.getTrack() != null && !discogsQueryDTO.getTrack().isBlank()) {
                log.info("Track specified in query. Applying filter and sorting results...");
                discogsFilterService.filterAndSortResults(discogsQueryDTO, results);
                log.info("Filtering and sorting completed");
            }

            DiscogsResultDTO resultDTO = discogsResultMapper.mapObjectToDTO(results, discogsQueryDTO);
            log.info("Search processing completed successfully for query: {}", discogsQueryDTO);

            return resultDTO;
        } catch (final DiscogsSearchException e) {
            log.error("DiscogsSearchException occurred while processing query: {}. Error: {}",
                    discogsQueryDTO, e.getMessage(), e);
            return new DiscogsResultDTO(); // Return an empty DTO or handle as per your error strategy
        } catch (final Exception e) {
            log.error(UNEXPECTED_ISSUE_OCCURRED +
                            " while processing query: {}. Error: {}",
                    discogsQueryDTO, e.getMessage(), e);
            throw new DiscogsSearchException(UNEXPECTED_ISSUE_OCCURRED, e);
        }
    }

    private static boolean isCompilationFormat(final DiscogsQueryDTO discogsQueryDTO) {
        boolean isCompilation = DiscogsFormats.COMP.getFormat().equalsIgnoreCase(discogsQueryDTO.getFormat())
                || DiscogsFormats.VINYL_COMPILATION.getFormat().equalsIgnoreCase(discogsQueryDTO.getFormat());
        log.debug("Checking if format is compilation: {}. Result: {}", discogsQueryDTO.getFormat(), isCompilation);
        return isCompilation;
    }

    private void correctUriForResultEntries(final DiscogsResult results) {
        log.debug("Correcting URIs for result entries");
        results.getResults().stream()
                .filter(entry -> !entry.getUri().contains(discogsUrlBuilder.getDiscogsWebsiteBaseUrl()))
                .forEach(entry -> entry.setUri(discogsUrlBuilder.getDiscogsWebsiteBaseUrl().concat(entry.getUri())));
        log.debug("URI correction completed");
    }

    private void processCompilationSearch(final DiscogsQueryDTO discogsQueryDTO, final DiscogsResult results) {
        log.debug("Generating compilation search URL for query: {}", discogsQueryDTO);
        String searchUrl = discogsUrlBuilder.generateCompilationSearchUrl(discogsQueryDTO);
        log.debug("Compilation search URL: {}", searchUrl);

        log.info("Sending compilation search request to Discogs API...");
        discogsAPIClient.getStringResultForQuery(searchUrl);
        DiscogsResult compResults = discogsAPIClient.getResultsForQuery(searchUrl);
        log.info("Received {} compilation results from Discogs API", compResults.getResults().size());

        Set<DiscogsEntry> entries = new HashSet<>(results.getResults());
        entries.addAll(compResults.getResults());
        results.setResults(new ArrayList<>(entries));

        log.debug("Merged original results with compilation results. Total results: {}", results.getResults().size());
    }

}