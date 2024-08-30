package org.discogs.query.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.exceptions.DiscogsAPIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

/**
 * A client component for interacting with the Discogs API.
 * <p>
 * This class uses {@link RestTemplate} to send HTTP requests to the Discogs API and handle responses.
 * It provides methods to fetch data from the API and process responses, including error handling and logging.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultDiscogsAPIClient implements DiscogsAPIClient {

    @Value("${discogs.agent}")
    private String discogsAgent;

    private final RestTemplate restTemplate;

    /**
     * Logs the API response for debugging purposes.
     *
     * @param response the {@link ResponseEntity} containing the API response to log
     */
    private void logApiResponse(final ResponseEntity<?> response) {
        if (response.getBody() != null) {
            log.info("Discogs API response: {}", response.getBody());
        } else {
            log.warn("Discogs API response is empty.");
        }
    }

    /**
     * Retrieves results from the Discogs API for a given search URL.
     *
     * @param searchUrl the URL to query the Discogs API
     * @return an instance of {@link DiscogsResult} containing the API response data
     * @throws DiscogsAPIException if an error occurs while fetching data from the Discogs API
     */
    @Override
    public DiscogsResult getResultsForQuery(final String searchUrl) {
        HttpHeaders headers = buildHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            var response = restTemplate.exchange(searchUrl, HttpMethod.GET, entity, DiscogsResult.class);
            logApiResponse(response);

            return Optional.ofNullable(response.getBody())
                    .orElse(new DiscogsResult());
        } catch (final Exception e) {
            log.error("Error occurred while fetching data from Discogs API", e);
            throw new DiscogsAPIException("Failed to fetch data from Discogs API", e);
        }
    }

    /**
     * Retrieves a string result from the Discogs API for a given search URL.
     *
     * @param searchUrl the URL to query the Discogs API
     * @return a {@link String} containing the API response data
     * @throws DiscogsAPIException if an error occurs while fetching data from the Discogs API
     */
    @Override
    public String getStringResultForQuery(final String searchUrl) {
        HttpHeaders headers = buildHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            var response = restTemplate.exchange(searchUrl, HttpMethod.GET, entity, String.class);
            logApiResponse(response);
            return response.getBody();
        } catch (final Exception e) {
            log.error("Error occurred while fetching data from Discogs API", e);
            throw new DiscogsAPIException("Failed to fetch data from Discogs API", e);
        }
    }

    /**
     * Builds the HTTP headers required for making requests to the Discogs API.
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