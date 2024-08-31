package org.discogs.query.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.DiscogsMarketplaceResult;
import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.exceptions.DiscogsAPIException;
import org.discogs.query.limits.RateLimiter;
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
import java.util.concurrent.TimeUnit;

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

    private static final String ERROR_OCCURRED_WHILE_FETCHING_DATA_FROM_DISCOGS_API
            = "Error occurred while fetching data from Discogs API";

    private static final String FAILED_TO_FETCH_DATA_FROM_DISCOGS_API = "Failed to fetch data from Discogs API";

    @Value("${discogs.agent}")
    private String discogsAgent;

    private final RestTemplate restTemplate;

    private final RateLimiter rateLimiter; // Set to desired rate limit

    /**
     * Retrieves results from the Discogs API for a given search URL.
     *
     * @param searchUrl the URL to query the Discogs API
     * @return an instance of {@link DiscogsResult} containing the API response data
     * @throws DiscogsAPIException if an error occurs while fetching data from the Discogs API
     */
    @Override
    public DiscogsResult getResultsForQuery(final String searchUrl) {
        waitForRateLimit();
        return executeRequest(searchUrl, DiscogsResult.class);
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
        waitForRateLimit();
        return executeRequest(searchUrl, String.class);
    }

    /**
     * Checks whether the given item is listed on the Discogs Marketplace.
     * This method sends an HTTP GET request to the provided URL and returns the marketplace details.
     *
     * @param url the URL pointing to the item on the Discogs Marketplace.
     * @return a {@link DiscogsMarketplaceResult} object containing the details of the item on the marketplace.
     * @throws DiscogsAPIException if an error occurs while fetching data from the Discogs API.
     */
    @Override
    public DiscogsMarketplaceResult checkIsOnMarketplace(final String url) {
        waitForRateLimit();
        return executeRequest(url, DiscogsMarketplaceResult.class);
    }

    /**
     * Executes an HTTP GET request to the given URL and returns the response as an instance of the specified type.
     *
     * @param url          the URL to query the Discogs API
     * @param responseType the class type of the response
     * @param <T>          the type of the response
     * @return an instance of the response type containing the API response data
     * @throws DiscogsAPIException if an error occurs while fetching data from the Discogs API
     */
    private <T> T executeRequest(final String url, final Class<T> responseType) {
        HttpHeaders headers = buildHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            Thread.sleep(2000);
            var response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
            logApiResponse(response);
            return Optional.ofNullable(response.getBody())
                    .orElseThrow(() -> new DiscogsAPIException(FAILED_TO_FETCH_DATA_FROM_DISCOGS_API));
        } catch (final Exception e) {
            log.error(ERROR_OCCURRED_WHILE_FETCHING_DATA_FROM_DISCOGS_API, e);
            throw new DiscogsAPIException(FAILED_TO_FETCH_DATA_FROM_DISCOGS_API, e);
        }
    }

    /**
     * Waits for the rate limiter to allow a request to proceed.
     * This method blocks until the rate limiter permits a request.
     */
    private void waitForRateLimit() {
        while (!rateLimiter.tryAcquire()) {
            try {
                TimeUnit.MILLISECONDS.sleep(100); // Adjust sleep time if needed
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Interrupted while waiting for rate limit", e);
            }
        }
    }

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
