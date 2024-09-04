package org.discogs.query.helpers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.DiscogsEntry;
import org.discogs.query.enums.DiscogQueryParams;
import org.discogs.query.enums.DiscogsTypes;
import org.discogs.query.model.DiscogsQueryDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Utility class for constructing URLs for Discogs API requests.
 * <p>
 * This class provides methods to build URLs for searching Discogs entries,
 * retrieving release details, and accessing marketplace information. It uses
 * the base URL and endpoint configurations specified in application properties
 * and supports dynamic query parameter construction.
 */
@Getter
@Slf4j
@Component
@RequiredArgsConstructor
public class DiscogsUrlBuilder {

    private final UriBuilderHelper uriBuilderHelper;

    @Value("${discogs.url}")
    private String discogsBaseUrl;

    @Value("${discogs.search}")
    private String discogsSearchEndpoint;

    @Value("${discogs.release}")
    private String releaseEndpoint;

    @Value("${discogs.page-size}")
    private int pageSize;

    @Value("${discogs.token}")
    private String token;

    @Value("${discogs.baseUrl}")
    private String discogsWebsiteBaseUrl;

    @Value("${discogs.marketplaceCheck}")
    private String marketplaceUrl;

    /**
     * Builds the search URL based on the provided query parameters.
     *
     * @param discogsQueryDTO the search query data transfer object
     *                        containing the search criteria
     * @return the fully constructed search URL with query parameters
     */
    public String buildSearchUrl(final DiscogsQueryDTO discogsQueryDTO) {
        log.debug("Building search URL with parameters: {}", discogsQueryDTO);

        UriComponentsBuilder uriBuilder =
                UriComponentsBuilder.fromHttpUrl(discogsBaseUrl.concat(discogsSearchEndpoint))
                        .queryParam("per_page", pageSize)
                        .queryParam("page", 1)
                        .queryParam("token", token);

        addQueryParams(uriBuilder, discogsQueryDTO);
        String searchUrl = uriBuilder
                .encode()
                .toUriString()
                .replace("%20", "+");

        log.debug("Generated search URL: {}", searchUrl);
        return searchUrl;
    }

    /**
     * Builds the release URL for the given DiscogsEntry.
     *
     * @param discogsEntry the Discogs entry containing the release ID
     * @return the fully constructed release URL
     */
    public String buildReleaseUrl(final DiscogsEntry discogsEntry) {
        log.debug("Building release URL for DiscogsEntry ID: {}",
                discogsEntry.getId());

        String releaseUrl =
                UriComponentsBuilder.fromHttpUrl(discogsBaseUrl.concat(releaseEndpoint)
                                .concat(String.valueOf(discogsEntry.getId())))
                        .queryParam("token", token)
                        .queryParam("curr_abbr", "GBP")
                        .toUriString();

        log.debug("Generated release URL: {}", releaseUrl);
        return releaseUrl;
    }

    /**
     * Builds the marketplace URL for the given DiscogsEntry.
     *
     * @param discogsEntry the Discogs entry containing the release ID
     * @return the fully constructed marketplace URL
     */
    public String buildMarketplaceUrl(final DiscogsEntry discogsEntry) {
        log.debug("Building marketplace URL for DiscogsEntry ID: {}",
                discogsEntry.getId());

        String searchUrl =
                UriComponentsBuilder.fromHttpUrl(discogsBaseUrl.concat(marketplaceUrl)
                                .concat(String.valueOf(discogsEntry.getId())))
                        .queryParam("token", token)
                        .queryParam("curr_abbr", "GBP")
                        .toUriString();

        log.debug("Generated marketplace URL: {}", searchUrl);
        return searchUrl;
    }

    /**
     * Adds query parameters to the given {@link UriComponentsBuilder} based
     * on the provided {@link DiscogsQueryDTO}.
     *
     * @param uriBuilder      the {@link UriComponentsBuilder} to which the
     *                        query parameters should be added
     * @param discogsQueryDTO the query data transfer object containing the
     *                        parameters to be added
     */
    private void addQueryParams(final UriComponentsBuilder uriBuilder,
                                final DiscogsQueryDTO discogsQueryDTO) {
        log.debug("Adding query parameters to URL: {}", discogsQueryDTO);

        uriBuilderHelper.addIfNotNullOrBlank(uriBuilder,
                DiscogQueryParams.ARTIST.getQueryType(),
                discogsQueryDTO.getArtist());

        uriBuilderHelper.addIfNotNullOrBlank(uriBuilder,
                DiscogQueryParams.ALBUM.getQueryType(),
                discogsQueryDTO.getAlbum());

        uriBuilderHelper.addIfNotNullOrBlank(uriBuilder,
                DiscogQueryParams.TRACK.getQueryType(),
                discogsQueryDTO.getTrack());

        uriBuilderHelper.addIfNotNullOrBlank(uriBuilder,
                DiscogQueryParams.FORMAT.getQueryType(),
                discogsQueryDTO.getFormat());

        if (discogsQueryDTO.getCountry() != null) {
            uriBuilderHelper.addIfNotNullOrBlank(uriBuilder,
                    DiscogQueryParams.COUNTRY.getQueryType(),
                    discogsQueryDTO.getCountry().getCountryName());
        }

        DiscogsTypes types = discogsQueryDTO.getTypes();
        if (types == null || DiscogsTypes.UNKNOWN == types) {
            types = DiscogsTypes.RELEASE;
        }
        uriBuilderHelper.addIfNotNull(uriBuilder,
                DiscogQueryParams.TYPE.getQueryType(), types.getType());
    }

    /**
     * Builds the search URL for a compilation album based on the provided
     * query parameters.
     *
     * @param discogsQueryDTO the search query data transfer object
     *                        containing the search criteria
     * @return the fully constructed search URL with query parameters for a
     * compilation album
     */
    public String generateCompilationSearchUrl(final DiscogsQueryDTO discogsQueryDTO) {
        log.debug("Building compilation search URL with parameters: {}",
                discogsQueryDTO);

        DiscogsQueryDTO dtoForUrl = DiscogsQueryDTO.builder()
                .country(discogsQueryDTO.getCountry())
                .format(discogsQueryDTO.getFormat().replace(" ", "+"))
                .types(discogsQueryDTO.getTypes())
                .album(discogsQueryDTO.getAlbum())
                .track(discogsQueryDTO.getTrack())
                .artist("Various")
                .build();

        UriComponentsBuilder uriBuilder =
                UriComponentsBuilder.fromHttpUrl(discogsBaseUrl.concat(discogsSearchEndpoint))
                        .queryParam("per_page", pageSize)
                        .queryParam("page", 1)
                        .queryParam("token", token);

        addQueryParams(uriBuilder, dtoForUrl);
        String compilationSearchUrl = uriBuilder
                .encode()
                .toUriString()
                .replace("%20", "+");

        log.debug("Generated compilation search URL: {}", compilationSearchUrl);
        return compilationSearchUrl;
    }
}
