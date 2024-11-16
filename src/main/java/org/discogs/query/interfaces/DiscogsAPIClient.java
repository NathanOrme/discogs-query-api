package org.discogs.query.interfaces;

import org.discogs.query.domain.api.DiscogsMarketplaceResult;
import org.discogs.query.domain.api.DiscogsRelease;
import org.discogs.query.domain.api.DiscogsResult;

/**
 * Interface for interacting with the Discogs API.
 * <p>
 * This interface defines methods for querying the Discogs API and retrieving
 * results in different formats.
 * Implementations of this interface should handle the specifics of making
 * HTTP requests to the Discogs API
 * and processing the responses.
 */
public interface DiscogsAPIClient {

    /**
     * Retrieves results from the Discogs API for a given search URL.
     *
     * @param searchUrl the URL to query the Discogs API
     * @return an instance of {@link DiscogsResult} containing the API
     * response data
     */
    DiscogsResult getResultsForQuery(String searchUrl);

    /**
     * Retrieves a string result from the Discogs API for a given search URL.
     *
     * @param searchUrl the URL to query the Discogs API
     * @return a {@link String} containing the API response data
     */
    String getStringResultForQuery(String searchUrl);

    /**
     * Searches the website to see if a release exists
     *
     * @param searchUrl URL to use
     * @return a {@link DiscogsResult} result if found on the website.
     */
    DiscogsRelease getRelease(String searchUrl);

    /**
     * Searches the website to see if a release exists ina  user's collection
     *
     * @param searchUrl URL to use
     * @return a {@link DiscogsResult} result if found on the website.
     */
    DiscogsRelease getCollectionReleases(String searchUrl);

    /**
     * Searches marketplace to see if release is on marketplace
     *
     * @param url URL to use for search
     * @return Result to say if on marketplace
     */
    DiscogsMarketplaceResult getMarketplaceResultForQuery(String url);

}