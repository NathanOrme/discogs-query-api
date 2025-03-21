package org.discogs.query.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing an entry in Discogs. This class encapsulates details
 * about a Discogs entry such as ID, title, format, and URLs.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DiscogsEntryDTO(
        int id,
        String title,
        List<String> format,
        @JsonProperty("master_url") String url,
        String uri,
        String country,
        String year,
        Boolean isOnMarketplace,
        Float lowestPrice,
        Integer numberForSale) {
}
