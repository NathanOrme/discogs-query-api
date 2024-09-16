package org.discogs.query.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing an entry in Discogs.
 * This class encapsulates details about a Discogs entry such as ID, title,
 * format, and URLs.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DiscogsEntryDTO(
        /**
         * The unique identifier for the Discogs entry.
         */
        int id,

        /**
         * The title of the Discogs entry.
         */
        String title,

        /**
         * A list of formats associated with the Discogs entry (e.g., vinyl, CD,
         * etc.).
         */
        List<String> format,

        /**
         * The URL of the master entry in Discogs.
         * This field is mapped to the JSON property "master_url".
         */
        @JsonProperty("master_url")
        String url,

        /**
         * The URI of the Discogs entry.
         */
        String uri,

        /**
         * The country for the Discogs entry.
         */
        String country,

        /**
         * The year for the Discogs entry.
         */
        String year,

        /**
         * Marks if on marketplace.
         */
        Boolean isOnMarketplace,

        /**
         * Lowest price of item.
         */
        Float lowestPrice,

        /**
         * Number of copies for sale.
         */
        Integer numberForSale
) {
}
