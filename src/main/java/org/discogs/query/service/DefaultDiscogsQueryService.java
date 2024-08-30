package org.discogs.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.enums.DiscogFormats;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Default implementation of the {@link DiscogsQueryService}.
 * This service handles the communication with the Discogs API to search for records based on the provided query.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultDiscogsQueryService implements DiscogsQueryService {

    @Value("${discogs.url}")
    private String discogsBaseUrl;

    @Value("${discogs.baseUrl}")
    private String discogsResultBaseUrl;

    @Value("${discogs.search}")
    private String discogsSearchEndpoint;

    @Value("${discogs.agent}")
    private String discogsAgent;

    @Value("${discogs.page-size}")
    private int pageSize;

    @Value("${discogs.token}")
    private String token;

    private final RestTemplate restTemplate;

    /**
     * Searches the Discogs database based on the provided query.
     *
     * @param discogsQueryDTO the search query data transfer object
     *                        containing artist, track, and optional format information
     * @return a {@link DiscogsResultDTO} object containing the search results
     */
    @Override
    public DiscogsResultDTO searchBasedOnQuery(final DiscogsQueryDTO discogsQueryDTO) {
        String searchUrl = buildSearchUrl(discogsQueryDTO);
        HttpHeaders headers = buildHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            var response = restTemplate.exchange(searchUrl, HttpMethod.GET, entity, DiscogsResultDTO.class);
            log.info("Discogs API response: {}", response.getBody());
            if (response.getBody() == null || response.getBody().getResults() == null || response.getBody().getResults().isEmpty()) {
                return DiscogsResultDTO.builder().build();
            }
            var responseBody = response.getBody();
            List<DiscogsEntryDTO> uniqueEntries = filterUniqueEntriesByMasterUrl(responseBody.getResults());
            uniqueEntries.forEach(discogsEntryDTO -> discogsEntryDTO.setUri(discogsResultBaseUrl.concat(discogsEntryDTO.getUri())));
            responseBody.setResults(uniqueEntries);  // Set the filtered entries back
            return responseBody;
        } catch (final Exception e) {
            log.error("Failed to fetch data from Discogs API", e);
            throw e;
        }
    }

    private List<DiscogsEntryDTO> filterUniqueEntriesByMasterUrl(final List<DiscogsEntryDTO> entries) {
        // Use a map to store the unique entries by masterUrl
        Map<String, DiscogsEntryDTO> uniqueEntriesMap = entries.stream()
                .filter(entry -> entry.getUrl() != null)  // Ensure masterUrl is not null
                .collect(Collectors.toMap(
                        DiscogsEntryDTO::getUrl,  // Key is the masterUrl
                        Function.identity(),  // Value is the DTO itself
                        (existing, replacement) -> existing  // If duplicate, keep the existing entry
                ));

        // Return the values of the map as a list
        return new ArrayList<>(uniqueEntriesMap.values());
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
            uriBuilder.queryParam("artist", discogsQueryDTO.getArtist());
        }
        if (discogsQueryDTO.getTrack() != null && !discogsQueryDTO.getTrack().isBlank()) {
            uriBuilder.queryParam("track", discogsQueryDTO.getTrack());
        }
        if (discogsQueryDTO.getFormat() != null && !discogsQueryDTO.getFormat().isBlank()) {
            uriBuilder.queryParam("format", discogsQueryDTO.getFormat());
        } else {
            // Default format if not provided
            uriBuilder.queryParam("format", DiscogFormats.VINYL_COMPILATION.getFormat());
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
     * Builds the HTTP headers required for making a request to the Discogs API.
     *
     * @return the constructed {@link HttpHeaders} object containing necessary headers
     */
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", discogsAgent);
        return headers;
    }
}