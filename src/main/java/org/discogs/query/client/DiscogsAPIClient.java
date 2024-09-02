package org.discogs.query.client;

import org.discogs.query.domain.DiscogsMarketplaceResult;
import org.discogs.query.domain.DiscogsRelease;
import org.discogs.query.domain.DiscogsResult;

/**
 * Interface for interacting with the Discogs API.
 * <p>
 * This interface defines methods for querying the Discogs API and retrieving results in different formats.
 * Implementations of this interface should handle the specifics of making HTTP requests to the Discogs API
 * and processing the responses.
 */
public interface DiscogsAPIClient {

    /**
     * Retrieves results from the Discogs API for a given search URL.
     *
     * @param searchUrl the URL to query the Discogs API
     * @return an instance of {@link DiscogsResult} containing the API response data
     */
    DiscogsResult getResultsForQuery(final String searchUrl);

    /**
     * Retrieves a string result from the Discogs API for a given search URL.
     *
     * @param searchUrl the URL to query the Discogs API
     * @return a {@link String} containing the API response data
     */
    String getStringResultForQuery(final String searchUrl);

    /**
     * Searches marketplace to see if release is on marketplace
     *
     * @param url URL to use for search
     * @return Result to say if on marketplace
     */
    DiscogsMarketplaceResult checkIsOnMarketplace(final String url);

    /**
     * Searches the website to see if a release exists
     *
     * @param searchUrl URL to use
     * @return a {@link DiscogsResult} result if found on the website.
     */
    DiscogsRelease getRelease(final String searchUrl);
}