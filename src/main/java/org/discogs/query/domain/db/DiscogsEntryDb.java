package org.discogs.query.domain.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing an entry in Discogs.
 * This class encapsulates details about a Discogs entry such as ID, title,
 * format, and URLs.
 */
@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscogsEntryDb {

    /**
     * ID for DB Purposes
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The unique identifier for the Discogs entry.
     */
    private int discogsId;

    /**
     * The title of the Discogs entry.
     */
    private String title;

    /**
     * A list of formats associated with the Discogs entry (e.g., vinyl, CD,
     * etc.).
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
    private Boolean isOnMarketplace;

    /**
     * Lowest price of item
     */
    private Float lowestPrice;

    /**
     * Number of copies for sale
     */
    private Integer numberForSale;

    /**
     * Reference to the DiscogsResultDb that owns this entry.
     */
    @ManyToOne
    @JoinColumn(name = "discogs_result_db_id") // Foreign key column
    private DiscogsResultDb discogsResultDb;
}
