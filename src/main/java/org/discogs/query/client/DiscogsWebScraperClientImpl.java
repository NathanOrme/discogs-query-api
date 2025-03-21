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
 * A client component for scraping the Discogs Marketplace website. This class scrapes the Discogs
 * Marketplace listings for a specific release and filters them by country (United Kingdom).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiscogsWebScraperClientImpl implements DiscogsWebScraperClient {

    public static final String ERROR_SCRAPING_THE_DISCOGS_MARKETPLACE_ATTEMPT =
            "Error scraping the Discogs Marketplace, attempt {}/{}";
    private static final String UK_FILTER = "?ev=rb&ships_from=United+Kingdom";

    private final HttpConfig httpConfig;
    private final JsoupHelper jsoupHelper;

    /**
     * Scrapes the Discogs Marketplace website for listings of a given release ID and filters them by
     * country (United Kingdom).
     *
     * @param releaseId the Discogs release ID
     * @return a list of {@link DiscogsWebsiteResult} containing marketplace listings
     */
    @Override
    public List<DiscogsWebsiteResult> getMarketplaceResultsForRelease(final String releaseId) {
        String url = "https://www.discogs.com/sell/release/" + releaseId + UK_FILTER;
        LogHelper.info(() -> "Fetching marketplace data for release ID: {}", releaseId);

        Document doc = fetchDocumentWithRetry(url);

        Elements listings = doc.select(".shortcut_navigable");
        if (listings.isEmpty()) {
            LogHelper.info(() -> "No listings found for release ID: {}", releaseId);
            return new ArrayList<>();
        }
        return processListings(listings);
    }

    /**
     * Tries to fetch the HTML document from the given URL with retries.
     *
     * @param url the URL to fetch
     * @return the fetched Document
     */
    private Document fetchDocumentWithRetry(final String url) {
        int maxRetries = 3;
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                return jsoupHelper.connect(url, httpConfig.buildHeaders().toSingleValueMap());
            } catch (final Exception e) {
                LogHelper.error(
                        () -> ERROR_SCRAPING_THE_DISCOGS_MARKETPLACE_ATTEMPT, retryCount + 1, maxRetries);
                retryCount++;
                waitBeforeRetry();
            }
        }
        String errorMessage =
                "Failed to scrape data from Discogs Marketplace after %s attempts".formatted(maxRetries);
        LogHelper.info(() -> errorMessage);
        throw new NoMarketplaceListingsException(errorMessage);
    }

    private void waitBeforeRetry() {
        try {
            Thread.sleep(200);
        } catch (final InterruptedException ie) {
            Thread.currentThread().interrupt();
            LogHelper.info(() -> "Thread was interrupted while waiting to retry", ie);
        }
    }

    private List<DiscogsWebsiteResult> processListings(final Elements listings) {
        List<DiscogsWebsiteResult> results = new ArrayList<>();
        for (final Element listing : listings) {
            DiscogsWebsiteResult result = extractResultFromListing(listing);
            if (result != null) {
                results.add(result);
            }
        }
        return results;
    }

    /**
     * Processes a single listing element and returns a DiscogsWebsiteResult if the listing meets the
     * criteria (i.e. it ships from the United Kingdom and contains seller info).
     *
     * @param listing the listing element
     * @return a DiscogsWebsiteResult or null if the listing should be skipped
     */
    private DiscogsWebsiteResult extractResultFromListing(final Element listing) {
        Elements sellerItems = listing.select("ul > li");

        String sellerName = null;
        String sellerRating = "No rating";
        String ratingCount = "0 ratings";
        Element priceElement = listing.selectFirst(".price");
        String price = priceElement != null ? priceElement.text() : "Unknown";
        Element conditionElement = listing.selectFirst(".item_condition span");
        String condition = conditionElement != null ? conditionElement.text() : "Unknown";

        for (final Element item : sellerItems) {
            Element sellerLabelElem = item.selectFirst("span.mplabel.seller_label");
            if (sellerLabelElem != null) {
                Element sellerNameElem = item.selectFirst("strong > a");
                if (sellerNameElem != null) {
                    sellerName = sellerNameElem.text();
                    LogHelper.debug(() -> "Found seller name: {}", sellerName);
                }
            } else {
                Element starRatingElem = item.selectFirst(".star_rating");
                if (starRatingElem != null) {
                    sellerRating = starRatingElem.attr("aria-label");
                    Element ratingCountElem = item.selectFirst("a.section_link");
                    if (ratingCountElem != null) {
                        ratingCount = ratingCountElem.text();
                    }
                    LogHelper.debug(
                            () -> "Found seller rating: {} with count: {}", sellerRating, ratingCount);
                } else if (item.text().contains("Ships From:")) {
                    if (!item.text().contains("United Kingdom")) {
                        LogHelper.debug(() -> "No items shipping from the UK: {}", item.text());
                        return null; // Skip this listing if it doesn't ship from the UK
                    } else {
                        LogHelper.debug(() -> "Item ships from the United Kingdom.");
                    }
                }
            }
        }

        if (sellerName != null) {
            DiscogsWebsiteResult result =
                    new DiscogsWebsiteResult(
                            price, condition, "United Kingdom", sellerName, sellerRating, ratingCount);
            LogHelper.info(() -> "Added result for seller: {}", sellerName);
            return result;
        } else {
            LogHelper.debug(() -> "Seller information not found for listing.");
            return null;
        }
    }
}
