package org.discogs.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.client.DiscogsAPIClient;
import org.discogs.query.domain.DiscogsEntry;
import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.enums.DiscogQueryParams;
import org.discogs.query.enums.DiscogsTypes;
import org.discogs.query.exceptions.DiscogsMarketplaceException;
import org.discogs.query.exceptions.DiscogsSearchException;
import org.discogs.query.helpers.UriBuilderHelper;
import org.discogs.query.interfaces.DiscogsQueryService;
import org.discogs.query.mapper.DiscogsResultMapper;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Comparator;

/**
 * Implementation of {@link DiscogsQueryService} that interacts with the Discogs API.
 * This service handles search requests and processes the API responses.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultDiscogsQueryService implements DiscogsQueryService {

    public static final String UNEXPECTED_ISSUE_OCCURRED = "Unexpected issue occurred";
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
    private final UriBuilderHelper uriBuilderHelper;

    /**
     * Searches the Discogs database based on the provided query.
     *
     * @param discogsQueryDTO the search query containing artist, track, and optional format information
     * @return a {@link DiscogsResultDTO} with the search results
     */
    @Override
    public DiscogsResultDTO searchBasedOnQuery(final DiscogsQueryDTO discogsQueryDTO) {
        DiscogsResultDTO discogsResultDTO = null;
        try {
            log.info("Processing query: {}", discogsQueryDTO);
            String searchUrl = buildSearchUrl(discogsQueryDTO);
            var results = discogsAPIClient.getResultsForQuery(searchUrl);
            log.info("Received {} results from search API", results.getResults().size());
            correctUriForResultEntries(results);
            discogsResultDTO = discogsResultMapper.mapObjectToDTO(results, discogsQueryDTO);
            if (Boolean.TRUE.equals(discogsQueryDTO.getCheckMarketplace())) {
                results.getResults().forEach(this::processOnMarketplace);
                orderResults(results);
            }
            log.info("Finished all http requests for: {}", discogsQueryDTO);
            return discogsResultMapper.mapObjectToDTO(results, discogsQueryDTO);
        } catch (final DiscogsMarketplaceException | DiscogsSearchException e) {
            log.error(UNEXPECTED_ISSUE_OCCURRED, e);
            return discogsResultDTO;
        } catch (final Exception e) {
            log.error(UNEXPECTED_ISSUE_OCCURRED, e);
            throw new DiscogsSearchException(UNEXPECTED_ISSUE_OCCURRED, e);
        }
    }

    private static void orderResults(final DiscogsResult results) {
        results.setResults(results.getResults().stream()
                .filter(entry -> entry.getLowestPrice() != null) // Filter entries with non-null lowestPrice
                .sorted(Comparator.comparing(DiscogsEntry::getLowestPrice)) // Sort based on lowestPrice
                .toList());
    }

    private void processOnMarketplace(final DiscogsEntry discogsEntry) {
        try {
            log.info("Checking marketplace for entry {}", discogsEntry);
            var marketplaceUrl = buildMarketplaceUrl(discogsEntry);
            var marketplaceResults = discogsAPIClient.checkIsOnMarketplace(marketplaceUrl);
            discogsEntry.setIsOnMarketplace(marketplaceResults.getNumberForSale() != null);
            discogsEntry.setLowestPrice(marketplaceResults.getResult() != null
                    ? marketplaceResults.getResult().getValue()
                    : Float.parseFloat("0"));
            log.info("Finished checking marketplace for {}", discogsEntry);
        } catch (final Exception e) {
            log.error(UNEXPECTED_ISSUE_OCCURRED, e);
        }
    }

    private String buildMarketplaceUrl(final DiscogsEntry discogsEntry) {
        var baseUrl = discogsBaseUrl.concat(marketplaceCheck)
                .concat(String.valueOf(discogsEntry.getId()));
        baseUrl = baseUrl.concat("?token=").concat(token);
        return baseUrl;
    }

    private void correctUriForResultEntries(final DiscogsResult results) {
        if (results.getResults() != null) {
            results.getResults().stream()
                .filter(entry -> !entry.getUri().contains(discogsWebsiteBaseUrl))
                .forEach(entry ->
                    entry.setUri(discogsWebsiteBaseUrl
                                 .concat(entry.getUri())));
        }
    }

    /**
     * Builds the search URL based on the base URL and query parameters.
     *
     * @param discogsQueryDTO the search query data transfer object
     * @return the fully constructed search URL with query parameters
     */
    private String buildSearchUrl(final DiscogsQueryDTO discogsQueryDTO) {
        log.info("Generating URL for query: {}", discogsQueryDTO);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(discogsBaseUrl + discogsSearchEndpoint);

        uriBuilderHelper.addIfNotNullOrBlank(uriBuilder,
                DiscogQueryParams.ARTIST.getQueryType(), discogsQueryDTO.getArtist());
        uriBuilderHelper.addIfNotNullOrBlank(uriBuilder,
                DiscogQueryParams.ALBUM.getQueryType(), discogsQueryDTO.getAlbum());
        uriBuilderHelper.addIfNotNullOrBlank(uriBuilder,
                DiscogQueryParams.TRACK.getQueryType(), discogsQueryDTO.getTrack());
        uriBuilderHelper.addIfNotNullOrBlank(uriBuilder,
                DiscogQueryParams.FORMAT.getQueryType(), discogsQueryDTO.getFormat());
        if (discogsQueryDTO.getCountry() != null) {
            uriBuilderHelper.addIfNotNullOrBlank(uriBuilder,
                    DiscogQueryParams.COUNTRY.getQueryType(), discogsQueryDTO.getCountry().getCountryName());
        }

        DiscogsTypes types = discogsQueryDTO.getTypes();
        if (types == null || DiscogsTypes.UNKNOWN == types) {
            types = DiscogsTypes.RELEASE;
        }
        uriBuilderHelper.addIfNotNull(uriBuilder, DiscogQueryParams.TYPE.getQueryType(), types.getType());


        uriBuilder.queryParam("per_page", pageSize);
        uriBuilder.queryParam("page", 1);
        uriBuilder.queryParam("token", token);

        // Build the URL and replace %20 with +
        String url = uriBuilder.toUriString();
        url = url.replace("%20", "+");
        log.info("Generated URL {} for Query {}", url, discogsQueryDTO);
        return url;
    }

}
