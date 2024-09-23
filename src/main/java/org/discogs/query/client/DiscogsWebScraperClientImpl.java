package org.discogs.query.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.config.HttpConfig;
import org.discogs.query.domain.website.DiscogsWebsiteResult;
import org.discogs.query.interfaces.DiscogsWebScraperClient;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    private final HttpConfig httpConfig;

    /**
     * Scrapes the Discogs Marketplace website for listings of a given release ID and filters them by country (United
     * Kingdom).
     *
     * @param releaseId the Discogs release ID
     * @return a list of {@link DiscogsWebsiteResult} containing marketplace listings
     */
    @Override
    public List<DiscogsWebsiteResult> getMarketplaceResultsForRelease(final String releaseId) {
        String url = "https://www.discogs.com/sell/release/" + releaseId;
        log.info("Fetching marketplace data for release ID: {}", releaseId);

        int maxRetries = 3; // Number of retry attempts
        int retryCount = 0; // Current retry count
        long delay = 2000; // Delay in milliseconds (2 seconds)

        while (retryCount < maxRetries) {
            try {
                // Fetch and parse the HTML from the Discogs marketplace page
                Document doc = Jsoup.connect(url)
                        .headers(httpConfig.buildHeaders().toSingleValueMap())
                        .get();
                Elements listings = doc.select(".shortcut_navigable");

                // Filter the listings by 'Ships From: United Kingdom'
                List<DiscogsWebsiteResult> results = new ArrayList<>();
                for (final Element listing : listings) {
                    // Get seller information
                    Element sellerInfo = listing.selectFirst(".seller_info");
                    if (sellerInfo != null) {
                        checkSellerInfo(listing, sellerInfo, results);
                    }
                }
                return results;
            } catch (final HttpStatusException e) {
                log.error("Error scraping the Discogs Marketplace, attempt {}/{}", retryCount + 1, maxRetries);
                retryCount++;

                // Wait before the next retry
                try {
                    Thread.sleep(delay);
                } catch (final InterruptedException ie) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    log.error("Thread was interrupted while waiting to retry", ie);
                    return new ArrayList<>(); // Return empty list if interrupted
                }
            } catch (final IOException e) {
                log.error("Error scraping the Discogs Marketplace, attempt {}/{}", retryCount + 1, maxRetries);
                retryCount++;

                // Wait before the next retry
                try {
                    Thread.sleep(delay);
                } catch (final InterruptedException ie) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    log.error("Thread was interrupted while waiting to retry", ie);
                    return new ArrayList<>(); // Return empty list if interrupted
                }
            }
        }

        log.error("Failed to scrape data from Discogs Marketplace after {} attempts", maxRetries);
        return new ArrayList<>(); // Return empty list if all retries fail
    }


    private void checkSellerInfo(final Element listing, final Element sellerInfo,
                                 final List<DiscogsWebsiteResult> results) {
        // Check if the listing ships from the United Kingdom
        Element shippingInfo = sellerInfo.selectFirst("li:containsOwn(Ships From:)");
        if (shippingInfo == null) {
            log.debug("No Shipping Info Found");
            return;
        }
        if (!shippingInfo.text().contains("United Kingdom")) {
            log.debug("No Items shipping from the UK");
        }
        // Extract seller name
        String sellerName = sellerInfo.selectFirst("strong > a").text();

        // Extract seller rating
        Element ratingElement = sellerInfo.selectFirst(".star_rating");
        String sellerRating = ratingElement != null ? ratingElement.attr("aria-label") : "No rating";

        // Extract number of ratings
        String ratingCount = sellerInfo.selectFirst("a.section_link").text();

        // Extract price and condition
        String price = listing.selectFirst(".price").text();
        String condition = listing.selectFirst(".item_condition span").text();

        // Create and add the result
        DiscogsWebsiteResult result = new DiscogsWebsiteResult(
                price, condition, "United Kingdom", sellerName, sellerRating, ratingCount
        );
        results.add(result);
    }
}

