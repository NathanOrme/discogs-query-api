package org.discogs.query.service;

import lombok.extern.slf4j.Slf4j;
import org.discogs.query.enums.DiscogFormats;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Default implementation of the {@link DiscogsQueryService}.
 * This service handles the communication with the Discogs API to search for records based on the provided query.
 */
@Slf4j
@Service
public class DefaultDiscogsQueryService implements DiscogsQueryService {

    @Value("${discogs.url}")
    private String discogsBaseUrl;

    @Value("${discogs.search}")
    private String discogsSearchEndpoint;

    @Value("${discogs.agent}")
    private String discogsAgent;

    @Value("${discogs.page-size}")
    private int pageSize;

    @Value("${discogs.token}")
    private String token;

    /**
     * Searches the Discogs database based on the provided query.
     *
     * @param discogsQueryDTO the search query data transfer object
     *                        containing artist, track, and optional format information
     * @return a {@link DiscogsResultDTO} object containing the search results
     */
    @Override
    public DiscogsResultDTO searchBasedOnQuery(final DiscogsQueryDTO discogsQueryDTO) {
        var searchUrl = buildSearchUrl(discogsBaseUrl.concat(discogsSearchEndpoint), discogsQueryDTO);
        var headers = buildHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        var restTemplate = new RestTemplate();
        var response = restTemplate.exchange(searchUrl, HttpMethod.GET, entity, DiscogsResultDTO.class);
        log.info(String.valueOf(response.getBody()));
        return response.getBody();
    }

    /**
     * Builds the search URL based on the base URL and query parameters.
     *
     * @param searchUrl       the base URL for the search endpoint
     * @param discogsQueryDTO the search query data transfer object
     * @return the fully constructed search URL with query parameters
     */
    private String buildSearchUrl(final String searchUrl, final DiscogsQueryDTO discogsQueryDTO) {
        var urlBuilder = searchUrl;
        var artist = discogsQueryDTO.getArtist().replace(" ", "+");
        urlBuilder = urlBuilder.concat("?artist=".concat(artist));
        var track = discogsQueryDTO.getTrack().replace(" ", "+");
        urlBuilder = appendUrlAsAppropriate(urlBuilder, "track", track);
        if (discogsQueryDTO.getFormat() != null && !discogsQueryDTO.getFormat().isBlank()) {
            urlBuilder = appendUrlAsAppropriate(urlBuilder, "format", discogsQueryDTO.getFormat());
        } else {
            urlBuilder = appendUrlAsAppropriate(urlBuilder, "format", DiscogFormats.COMP.getFormat());
        }
        urlBuilder = urlBuilder.concat("&per_page=").concat(String.valueOf(pageSize)).concat("&page=1");
        urlBuilder = urlBuilder.concat("&token=").concat(token);
        return urlBuilder;
    }

    /**
     * Builds the HTTP headers required for making a request to the Discogs API.
     *
     * @return the constructed {@link HttpHeaders} object containing necessary headers
     */
    private HttpHeaders buildHeaders() {
        var headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", discogsAgent);
        return headers;
    }

    /**
     * Appends a query parameter to the URL based on the field name and value.
     *
     * @param urlBuilder     the current URL being constructed
     * @param field          the query parameter field name
     * @param stringToAppend the value to append for the field
     * @return the updated URL with the appended query parameter
     */
    private String appendUrlAsAppropriate(final String urlBuilder, final String field, final String stringToAppend) {
        if (urlBuilder.contains("?")) {
            return urlBuilder.concat("&".concat(field).concat("=").concat(stringToAppend));
        } else {
            return urlBuilder.concat("?".concat(field).concat("=").concat(stringToAppend));
        }
    }
}