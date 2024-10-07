package org.discogs.query.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.config.HttpConfig;
import org.discogs.query.domain.website.DiscogsWebsiteResult;
import org.discogs.query.exceptions.NoMarketplaceListingsException;
import org.discogs.query.helpers.JsoupHelper;
import org.discogs.query.helpers.LogHelper;
import org.discogs.query.interfaces.DiscogsWebScraperClient;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * A client component for scraping the Discogs Marketplace website.
 * This class scrapes the Discogs Marketplace listings for a specific release
 * and filters them by country (United Kingdom).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiscogsWebScraperClientImpl implements DiscogsWebScraperClient {

    public static final String ERROR_SCRAPING_THE_DISCOGS_MARKETPLACE_ATTEMPT = "Error scraping the Discogs " +
            "Marketplace, attempt {}/{}";
    private final HttpConfig httpConfig;
    private final JsoupHelper jsoupHelper;

    /**
     * Scrapes the Discogs Marketplace website for listings of a given release ID and filters them by country (United
     * Kingdom).
     *
     * @param releaseId the Discogs release ID
     * @return a list of {@link DiscogsWebsiteResult} containing marketplace listings
     */
    @Override
    public List<DiscogsWebsiteResult> getMarketplaceResultsForRelease(final String releaseId) {
        // Construct the URL with the filter for United Kingdom
        String url = "https://www.discogs.com/sell/release/" + releaseId + "?ev=rb&ships_from=United+Kingdom";
        LogHelper.info(log, () -> "Fetching marketplace data for release ID: {}", releaseId);

        Document doc = fetchDocumentWithRetry(url); // Call the refactored retry logic for jsoupHelper

        // Process the document if it was fetched successfully
        Elements listings = doc.select(".shortcut_navigable");

        // Check if any listings exist
        if (listings.isEmpty()) {
            LogHelper.info(log, () -> "No listings found for release ID: {}", releaseId);
            return new ArrayList<>(); // Return empty list if no listings
        }

        // Process each listing
        return processListings(listings);
    }

    /**
     * Tries to fetch the HTML document from the given URL with retries.
     *
     * @param url the URL to fetch
     * @return the fetched Document
     */
    private Document fetchDocumentWithRetry(final String url) {
        int maxRetries = 3; // Number of retry attempts
        int retryCount = 0; // Current retry count

        while (retryCount < maxRetries) {
            try {
                // Fetch and parse the HTML from the Discogs marketplace page using JsoupHelper
                return jsoupHelper.connect(url, httpConfig.buildHeaders().toSingleValueMap());
            } catch (final Exception e) {
                LogHelper.error(log, () -> ERROR_SCRAPING_THE_DISCOGS_MARKETPLACE_ATTEMPT, retryCount + 1, maxRetries);
                retryCount++;
                waitBeforeRetry();
            }
        }

        String errorMessage = "Failed to scrape data from Discogs Marketplace after %s attempts".formatted(maxRetries);
        LogHelper.info(log, () -> errorMessage);
        throw new NoMarketplaceListingsException(errorMessage);
    }

    private void waitBeforeRetry() {
        try {
            Thread.sleep(200);
        } catch (final InterruptedException ie) {
            Thread.currentThread().interrupt();
            LogHelper.info(log, () -> "Thread was interrupted while waiting to retry", ie);
        }
    }

    private List<DiscogsWebsiteResult> processListings(final Elements listings) {
        List<DiscogsWebsiteResult> results = new ArrayList<>();
        for (final Element listing : listings) {
            checkSellerInfo(listing, results);
        }
        return results;
    }

    private void checkSellerInfo(final Element listing, final List<DiscogsWebsiteResult> results) {
        Elements sellerItems = listing.select("ul > li");

        String sellerName = null;
        String sellerRating = "No rating";
        String ratingCount = "0 ratings";
        String price = listing.selectFirst(".price").text();
        String condition = listing.selectFirst(".item_condition span").text();

        for (final Element item : sellerItems) {
            if (item.selectFirst("span.mplabel.seller_label") != null) {
                sellerName = item.selectFirst("strong > a").text();
                LogHelper.debug(log, () -> "Found seller name: {}", sellerName);
            } else if (item.selectFirst(".star_rating") != null) {
                sellerRating = item.selectFirst(".star_rating").attr("aria-label");
                ratingCount = item.selectFirst("a.section_link").text();
                LogHelper.debug(log, () -> "Found seller rating: {} with count: {}", sellerRating, ratingCount);
            } else if (item.text().contains("Ships From:")) {
                if (!item.text().contains("United Kingdom")) {
                    LogHelper.debug(log, () -> "No items shipping from the UK: {}", item.text());
                    return; // Exit if not shipping from the UK
                } else {
                    LogHelper.debug(log, () -> "Item ships from the United Kingdom.");
                }
            }
        }

        if (sellerName != null) {
            DiscogsWebsiteResult result = new DiscogsWebsiteResult(
                    price, condition, "United Kingdom", sellerName, sellerRating, ratingCount
            );
            results.add(result);
            LogHelper.info(log, () -> "Added result for seller: {}", sellerName);
        } else {
            LogHelper.debug(log, () -> "Seller information not found for listing.");
        }
    }
}