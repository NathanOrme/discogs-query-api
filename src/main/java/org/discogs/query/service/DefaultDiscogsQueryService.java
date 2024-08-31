package org.discogs.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.client.DiscogsAPIClient;
import org.discogs.query.domain.DiscogsEntry;
import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.enums.DiscogFormats;
import org.discogs.query.enums.DiscogQueryParams;
import org.discogs.query.enums.DiscogsTypes;
import org.discogs.query.exceptions.DiscogsAPIException;
import org.discogs.query.mapper.DiscogsResultMapper;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Implementation of {@link DiscogsQueryService} that interacts with the Discogs API.
 * This service handles search requests and processes the API responses.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultDiscogsQueryService implements DiscogsQueryService {

    @Value("${discogs.url}")
    private String discogsBaseUrl;

    @Value("${discogs.baseUrl}")
    private String discogsWebsiteBaseUrl;

    @Value("${discogs.search}")
    private String discogsSearchEndpoint;

    @Value("${discogs.marketplaceCheck}")
    private String marketplaceCheck;

    @Value("${discogs.page-size}")
    private int pageSize;

    @Value("${discogs.token}")
    private String token;

    private final DiscogsResultMapper discogsResultMapper;
    private final DiscogsAPIClient discogsAPIClient;

    /**
     * Searches the Discogs database based on the provided query.
     *
     * @param discogsQueryDTO the search query containing artist, track, and optional format information
     * @return a {@link DiscogsResultDTO} with the search results
     */
    @Override
    public DiscogsResultDTO searchBasedOnQuery(final DiscogsQueryDTO discogsQueryDTO) {
        try {
            String searchUrl = buildSearchUrl(discogsQueryDTO);
            var results = discogsAPIClient.getResultsForQuery(searchUrl);
            correctUriForResultEntries(results);
            results.getResults().forEach(discogsEntry -> {
                var marketplaceUrl = buildMarketplaceUrl(discogsEntry);
                var marketplaceResults = discogsAPIClient.checkIsOnMarketplace(marketplaceUrl);
                discogsEntry.setOnMarketplace(marketplaceResults.getNumberForSale() != null);
                discogsEntry.setLowestPrice(marketplaceResults.getResult() != null
                        ? marketplaceResults.getResult().getValue()
                        : Float.valueOf("0"));
            });
            results.setResults(results.getResults().stream()
                    .sorted(Comparator.comparing(DiscogsEntry::getLowestPrice))
                    .toList());
            return discogsResultMapper.mapObjectToDTO(results, discogsQueryDTO);
        } catch (final Exception e) {
            log.error("Unexpected issue occurred", e);
            throw new DiscogsAPIException("Unexpected issue occurred", e);
        }
    }

    private String buildMarketplaceUrl(final DiscogsEntry discogsEntry) {
        var baseUrl = discogsBaseUrl.concat(marketplaceCheck).concat(String.valueOf(discogsEntry.getId()));
        baseUrl = baseUrl.concat("?token=").concat(token);
        return baseUrl;
    }

    private void correctUriForResultEntries(final DiscogsResult results) {
        if (results.getResults() != null) {
            List<DiscogsEntry> uniqueEntries = filterUniqueEntriesByMasterUrl(results.getResults());
            uniqueEntries.forEach(entry -> entry.setUri(discogsWebsiteBaseUrl.concat(entry.getUri())));
            results.setResults(uniqueEntries);
        }
    }

    /**
     * Builds the search URL based on the base URL and query parameters.
     *
     * @param discogsQueryDTO the search query data transfer object
     * @return the fully constructed search URL with query parameters
     */
    private String buildSearchUrl(final DiscogsQueryDTO discogsQueryDTO) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(discogsBaseUrl + discogsSearchEndpoint);

        // Add parameters only if they are not null or empty
        if (discogsQueryDTO.getArtist() != null && !discogsQueryDTO.getArtist().isBlank()) {
            uriBuilder.queryParam(DiscogQueryParams.ARTIST.getQueryType(), discogsQueryDTO.getArtist());
        }

        if (discogsQueryDTO.getTrack() != null && !discogsQueryDTO.getTrack().isBlank()) {
            uriBuilder.queryParam(DiscogQueryParams.TRACK.getQueryType(), discogsQueryDTO.getTrack());
        }
        if (discogsQueryDTO.getFormat() != null && !discogsQueryDTO.getFormat().isBlank()) {
            uriBuilder.queryParam(DiscogQueryParams.FORMAT.getQueryType(), discogsQueryDTO.getFormat());
        } else {
            // Default format if not provided
            uriBuilder.queryParam(DiscogQueryParams.FORMAT.getQueryType(), DiscogFormats.VINYL_COMPILATION.getFormat());
        }
        if (discogsQueryDTO.getFormat() != null && !discogsQueryDTO.getFormat().isBlank()) {
            uriBuilder.queryParam(DiscogQueryParams.TYPE.getQueryType(), discogsQueryDTO.getFormat());
        } else {
            // Default format if not provided
            uriBuilder.queryParam(DiscogQueryParams.TYPE.getQueryType(), DiscogsTypes.RELEASE.getType());
        }


        uriBuilder.queryParam("per_page", pageSize);
        uriBuilder.queryParam("page", 1);
        uriBuilder.queryParam("token", token);

        // Build the URL and replace %20 with +
        String url = uriBuilder.toUriString();
        url = url.replace("%20", "+");
        return url;
    }

    /**
     * Filters entries to ensure that each master URL is unique.
     *
     * @param entries the list of entries to filter
     * @return a list of unique entries
     */
    private List<DiscogsEntry> filterUniqueEntriesByMasterUrl(final List<DiscogsEntry> entries) {
        return new ArrayList<>(entries.stream()
                .filter(entry -> entry.getUrl() != null)
                .collect(Collectors.toMap(
                        DiscogsEntry::getUrl,
                        entry -> entry,
                        (existing, replacement) -> existing
                ))
                .values());
    }
}