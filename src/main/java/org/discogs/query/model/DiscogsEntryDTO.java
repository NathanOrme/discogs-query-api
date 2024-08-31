package org.discogs.query.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing an entry in Discogs.
 * This class encapsulates details about a Discogs entry such as ID, title, format, and URLs.
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscogsEntryDTO {

    /**
     * The unique identifier for the Discogs entry.
     */
    private int id;

    /**
     * The title of the Discogs entry.
     */
    private String title;

    /**
     * A list of formats associated with the Discogs entry (e.g., vinyl, CD, etc.).
     */
    private List<String> format;

    /**
     * The URL of the master entry in Discogs.
     * This field is mapped to the JSON property "master_url".
     */
    @JsonProperty("master_url")
    private String url;

    /**
     * The URI of the Discogs entry.
     */
    private String uri;

    /**
     * The country for the Discogs entry
     */
    private String country;

    /**
     * The year for the Discogs entry
     */
    private String year;

    /**
     * Marks if on marketplace
     */
    private boolean isOnMarketplace;

    /**
     * Lowest price of item
     */
    private Float lowestPrice;
}