package org.discogs.query.interfaces;

import org.discogs.query.domain.website.DiscogsWebsiteResult;

import java.util.List;

public interface DiscogsWebScraperClient {

    List<DiscogsWebsiteResult> getMarketplaceResultsForRelease(String releaseId);
}
