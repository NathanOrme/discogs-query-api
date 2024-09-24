package org.discogs.query.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.config.HttpConfig;
import org.discogs.query.domain.website.DiscogsWebsiteResult;
import org.discogs.query.exceptions.NoMarketplaceListingsException;
import org.discogs.query.helpers.JsoupHelper;
import org.discogs.query.interfaces.DiscogsWebScraperClient;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "discogsResults", key = "#searchUrl")
    public List<DiscogsWebsiteResult> getMarketplaceResultsForRelease(final String releaseId) {
        // Construct the URL with the filter for United Kingdom
        String url = "https://www.discogs.com/sell/release/" + releaseId + "?ev=rb&ships_from=United+Kingdom";
        log.info("Fetching marketplace data for release ID: {}", releaseId);

        int maxRetries = 3; // Number of retry attempts
        int retryCount = 0; // Current retry count

        while (retryCount < maxRetries) {
            try {
                // Fetch and parse the HTML from the Discogs marketplace page
                Document doc = jsoupHelper.connect(url, httpConfig.buildHeaders().toSingleValueMap());
                Elements listings = doc.select(".shortcut_navigable");

                // Check if any listings exist
                if (listings.isEmpty()) {
                    log.info("No listings found for release ID: {}", releaseId);
                    return new ArrayList<>(); // Return empty list if no listings
                }

                // Process each listing
                return processListings(listings);
            } catch (final Exception e) {
                log.error(ERROR_SCRAPING_THE_DISCOGS_MARKETPLACE_ATTEMPT, retryCount + 1, maxRetries);
                retryCount++;
                waitBeforeRetry();
            }
        }

        String errorMessage = "Failed to scrape data from Discogs Marketplace after %s attempts".formatted(maxRetries);
        log.error(errorMessage);
        throw new NoMarketplaceListingsException(errorMessage);
    }

    private void waitBeforeRetry() {
        try {
            Thread.sleep(500);
        } catch (final InterruptedException ie) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            log.error("Thread was interrupted while waiting to retry", ie);
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
                log.debug("Found seller name: {}", sellerName);
            } else if (item.selectFirst(".star_rating") != null) {
                sellerRating = item.selectFirst(".star_rating").attr("aria-label");
                ratingCount = item.selectFirst("a.section_link").text();
                log.debug("Found seller rating: {} with count: {}", sellerRating, ratingCount);
            } else if (item.text().contains("Ships From:")) {
                if (!item.text().contains("United Kingdom")) {
                    log.debug("No items shipping from the UK: {}", item.text());
                    return; // Exit if not shipping from the UK
                } else {
                    log.debug("Item ships from the United Kingdom.");
                }
            }
        }

        if (sellerName != null) {
            DiscogsWebsiteResult result = new DiscogsWebsiteResult(
                    price, condition, "United Kingdom", sellerName, sellerRating, ratingCount
            );
            results.add(result);
            log.info("Added result for seller: {}", sellerName);
        } else {
            log.debug("Seller information not found for listing.");
        }
    }
}
