package org.discogs.query.interfaces;

import org.discogs.query.domain.website.DiscogsWebsiteResult;

import java.util.List;

/**
 * Client interface for interacting with Discogs website to scrape marketplace results.
 * This interface provides methods to retrieve marketplace listings for a specific release.
 */
public interface DiscogsWebScraperClient {

    /**
     * Retrieves marketplace results for a specific Discogs release by scraping the website.
     *
     * @param releaseId the ID of the release to retrieve marketplace listings for
     * @return a list of {@link DiscogsWebsiteResult} containing the marketplace results
     */
    List<DiscogsWebsiteResult> getMarketplaceResultsForRelease(String releaseId);
}
