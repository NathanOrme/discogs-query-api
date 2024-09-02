package org.discogs.query.helpers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.discogs.query.domain.DiscogsEntry;
import org.discogs.query.enums.DiscogQueryParams;
import org.discogs.query.enums.DiscogsTypes;
import org.discogs.query.model.DiscogsQueryDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Utility class to build URLs for Discogs API requests.
 */
@Getter
@Component
@RequiredArgsConstructor
public class DiscogsUrlBuilder {

    @Value("${discogs.url}")
    String discogsBaseUrl;

    @Value("${discogs.search}")
    String discogsSearchEndpoint;

    @Value("${discogs.release}")
    String releaseEndpoint;

    @Value("${discogs.page-size}")
    int pageSize;

    @Value("${discogs.token}")
    String token;

    @Value("${discogs.baseUrl}")
    String discogsWebsiteBaseUrl;

    private final UriBuilderHelper uriBuilderHelper;

    /**
     * Builds the search URL based on the provided query parameters.
     *
     * @param discogsQueryDTO the search query data transfer object containing the search criteria
     * @return the fully constructed search URL with query parameters
     */
    public String buildSearchUrl(final DiscogsQueryDTO discogsQueryDTO) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(discogsBaseUrl.concat(discogsSearchEndpoint))
                .queryParam("per_page", pageSize)
                .queryParam("page", 1)
                .queryParam("token", token);

        addQueryParams(uriBuilder, discogsQueryDTO);
        return uriBuilder.toUriString().replace("%20", "+");
    }

    /**
     * Builds the release URL for the given DiscogsEntry.
     *
     * @param discogsEntry the Discogs entry containing the release ID
     * @return the fully constructed release URL
     */
    public String buildReleaseUrl(final DiscogsEntry discogsEntry) {
        return UriComponentsBuilder.fromHttpUrl(discogsBaseUrl.concat(releaseEndpoint)
                        .concat(String.valueOf(discogsEntry.getId())))
                .queryParam("token", token)
                .queryParam("curr_abbr", "GBP")
                .toUriString();
    }

    private void addQueryParams(final UriComponentsBuilder uriBuilder, final DiscogsQueryDTO discogsQueryDTO) {
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
        uriBuilderHelper.addIfNotNull(uriBuilder, DiscogQueryParams.TYPE.getQueryType(), types.getType());
    }

}
