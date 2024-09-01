package org.discogs.query.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.DiscogsMarketplaceResult;
import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.exceptions.DiscogsMarketplaceException;
import org.discogs.query.exceptions.DiscogsSearchException;
import org.discogs.query.interfaces.HttpRequestService;
import org.discogs.query.interfaces.RateLimiterService;
import org.discogs.query.interfaces.RetryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

/**
 * A client component for interacting with the Discogs API.
 * <p>
 * This class uses {@link HttpRequestService} to send HTTP requests to the Discogs API,
 * handles responses, and manages retries and rate limits using {@link RetryService}
 * and {@link RateLimiterService}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultDiscogsAPIClient implements DiscogsAPIClient {

    @Value("${discogs.agent}")
    private String discogsAgent;

    private final HttpRequestService httpRequestService;
    private final RateLimiterService rateLimiterService;
    private final RetryService retryService;

    /**
     * Retrieves results from the Discogs API for a given search URL.
     *
     * @param searchUrl the URL to query the Discogs API
     * @return an instance of {@link DiscogsResult} containing the API response data
     * @throws DiscogsSearchException if an error occurs while fetching data from the Discogs API
     */
    @Cacheable(value = "discogsResults", key = "#searchUrl")
    @Override
    public DiscogsResult getResultsForQuery(final String searchUrl) {
        return executeWithRateLimitAndRetry(() -> httpRequestService.executeRequest(searchUrl, DiscogsResult.class),
                "Discogs Search API Request");
    }

    /**
     * Retrieves a string result from the Discogs API for a given search URL.
     *
     * @param searchUrl the URL to query the Discogs API
     * @return a {@link String} containing the API response data
     * @throws DiscogsSearchException if an error occurs while fetching data from the Discogs API
     */
    @Cacheable(value = "discogsResults", key = "#searchUrl")
    @Override
    public String getStringResultForQuery(final String searchUrl) {
        return executeWithRateLimitAndRetry(() -> httpRequestService.executeRequest(searchUrl, String.class),
                "Discogs Search API Request");
    }

    /**
     * Checks whether the given item is listed on the Discogs Marketplace.
     * This method sends an HTTP GET request to the provided URL and returns the marketplace details.
     *
     * @param url the URL pointing to the item on the Discogs Marketplace
     * @return a {@link DiscogsMarketplaceResult} object containing the details of the item on the marketplace
     * @throws DiscogsSearchException if an error occurs while fetching data from the Discogs API
     */
    @Cacheable(value = "discogsMarketplaceResults", key = "#url")
    @Override
    public DiscogsMarketplaceResult checkIsOnMarketplace(final String url) {
        return executeWithRateLimitAndRetry(() -> httpRequestService
                        .executeRequest(url, DiscogsMarketplaceResult.class),
                "Discogs Marketplace API Request");
    }

    /**
     * Executes a callable action with rate limit and retry logic.
     * <p>
     * This method ensures the rate limit is respected before executing the action and retries the action
     * in case of failure.
     *
     * @param action            the callable action to be executed
     * @param actionDescription a description of the action being performed
     * @param <T>               the type of the result returned by the action
     * @return the result of the action
     * @throws DiscogsSearchException      if an error occurs while fetching
     *                                     data from the Discogs API after all retry attempts
     * @throws DiscogsMarketplaceException if an error occurs while fetching
     *                                     data from the Discogs Marketplace API after all retry attempts
     */
    private <T> T executeWithRateLimitAndRetry(final Callable<T> action, final String actionDescription) {
        try {
            rateLimiterService.waitForRateLimit();  // Ensure rate limit before executing the action
            return retryService.executeWithRetry(action, actionDescription);
        } catch (final Exception e) {
            if (DiscogsMarketplaceResult.class.isAssignableFrom(action.getClass())) {
                throw new DiscogsMarketplaceException("Failed to fetch data from Discogs Marketplace API", e);
            }
            throw new DiscogsSearchException("Failed to fetch data from Discogs API", e);
        }
    }
}