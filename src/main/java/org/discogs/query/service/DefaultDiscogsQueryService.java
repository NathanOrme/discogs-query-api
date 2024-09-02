package org.discogs.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.client.DiscogsAPIClient;
import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.exceptions.DiscogsSearchException;
import org.discogs.query.helpers.DiscogsUrlBuilder;
import org.discogs.query.interfaces.DiscogsFilterService;
import org.discogs.query.interfaces.DiscogsQueryService;
import org.discogs.query.mapper.DiscogsResultMapper;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.stereotype.Service;

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
            log.info("Processing query: {}", discogsQueryDTO);
            String searchUrl = discogsUrlBuilder.buildSearchUrl(discogsQueryDTO);
            DiscogsResult results = discogsAPIClient.getResultsForQuery(searchUrl);
            log.info("Received {} results from search API", results.getResults().size());

            correctUriForResultEntries(results);

            if (discogsQueryDTO.getTrack() != null && !discogsQueryDTO.getTrack().isBlank()) {
                discogsFilterService.filterAndSortResults(discogsQueryDTO, results);
            }

            return discogsResultMapper.mapObjectToDTO(results, discogsQueryDTO);
        } catch (final DiscogsSearchException e) {
            log.error(UNEXPECTED_ISSUE_OCCURRED, e);
            return new DiscogsResultDTO(); // Return an empty DTO or handle as per your error strategy
        } catch (final Exception e) {
            log.error(UNEXPECTED_ISSUE_OCCURRED, e);
            throw new DiscogsSearchException(UNEXPECTED_ISSUE_OCCURRED, e);
        }
    }

    private void correctUriForResultEntries(final DiscogsResult results) {
        results.getResults().stream()
                .filter(entry -> !entry.getUri().contains(discogsUrlBuilder.getDiscogsWebsiteBaseUrl()))
                .forEach(entry -> entry.setUri(discogsUrlBuilder.getDiscogsWebsiteBaseUrl().concat(entry.getUri())));
    }
}
