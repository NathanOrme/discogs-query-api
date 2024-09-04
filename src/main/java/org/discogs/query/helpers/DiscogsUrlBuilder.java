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
 * Utility class to build URLs for Discogs API requests.
 * <p>
 * This class constructs various URLs required for querying the Discogs API.
 * It uses the
 * {@link UriComponentsBuilder} to build the URLs based on the provided
 * configuration values and
 * query parameters. The configuration values are injected from application
 * properties using
 * Spring's {@link Value} annotation.
 * </p>
 */
@Getter
@Slf4j
@Component
@RequiredArgsConstructor
public class DiscogsUrlBuilder {

    private final UriBuilderHelper uriBuilderHelper;

    /**
     * The base URL for the Discogs API.
     * <p>
     * This value is injected from the application properties with the key
     * "discogs.url".
     * </p>
     */
    @Value("${discogs.url}")
    String discogsBaseUrl;

    /**
     * The endpoint URL for searching within the Discogs API.
     * <p>
     * This value is injected from the application properties with the key
     * "discogs.search".
     * </p>
     */
    @Value("${discogs.search}")
    String discogsSearchEndpoint;

    /**
     * The endpoint URL for retrieving release information from the Discogs API.
     * <p>
     * This value is injected from the application properties with the key
     * "discogs.release".
     * </p>
     */
    @Value("${discogs.release}")
    String releaseEndpoint;

    /**
     * The default page size for search results.
     * <p>
     * This value is injected from the application properties with the key
     * "discogs.page-size".
     * </p>
     */
    @Value("${discogs.page-size}")
    int pageSize;

    /**
     * The API token used for authenticating requests to the Discogs API.
     * <p>
     * This value is injected from the application properties with the key
     * "discogs.token".
     * </p>
     */
    @Value("${discogs.token}")
    String token;

    /**
     * The base URL for the Discogs website.
     * <p>
     * This value is injected from the application properties with the key
     * "discogs.baseUrl".
     * </p>
     */
    @Value("${discogs.baseUrl}")
    String discogsWebsiteBaseUrl;

    /**
     * The URL for checking marketplace information on the Discogs API.
     * <p>
     * This value is injected from the application properties with the key
     * "discogs.marketplaceCheck".
     * </p>
     */
    @Value("${discogs.marketplaceCheck}")
    String marketplaceUrl;

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
                UriComponentsBuilder.fromHttpUrl(
                                discogsBaseUrl.concat(discogsSearchEndpoint))
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
                UriComponentsBuilder.fromHttpUrl(
                                discogsBaseUrl.concat(releaseEndpoint)
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

        String releaseUrl =
                UriComponentsBuilder.fromHttpUrl(
                                discogsBaseUrl.concat(marketplaceUrl)
                                        .concat(String.valueOf(discogsEntry.getId())))
                        .queryParam("token", token)
                        .queryParam("curr_abbr", "GBP")
                        .toUriString();

        log.debug("Generated marketplace URL: {}", releaseUrl);
        return releaseUrl;
    }

    /**
     * Adds query parameters to the given {@link UriComponentsBuilder} based
     * on the provided
     * {@link DiscogsQueryDTO}.
     *
     * @param uriBuilder      the {@link UriComponentsBuilder} to which query
     *                        parameters will be added
     * @param discogsQueryDTO the search query data transfer object
     *                        containing the search criteria
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
     * @return the fully constructed compilation search URL with query
     * parameters
     */
    public String generateCompilationSearchUrl(
            final DiscogsQueryDTO discogsQueryDTO) {
        log.debug("Building compilation search URL with parameters: {}",
                discogsQueryDTO);

        DiscogsQueryDTO dtoForUrl = DiscogsQueryDTO.builder()
                .country(discogsQueryDTO.getCountry())
                .format(discogsQueryDTO.getFormat()
                        .replace(" ", "+"))
                .types(discogsQueryDTO.getTypes())
                .album(discogsQueryDTO.getAlbum())
                .track(discogsQueryDTO.getTrack())
                .artist("Various")
                .build();

        UriComponentsBuilder uriBuilder =
                UriComponentsBuilder.fromHttpUrl(
                                discogsBaseUrl.concat(discogsSearchEndpoint))
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
