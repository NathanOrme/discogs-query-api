package org.discogs.query.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.discogs.query.domain.release.Artist;
import org.discogs.query.domain.release.ExtraArtist;
import org.discogs.query.domain.release.Format;
import org.discogs.query.domain.release.Label;
import org.discogs.query.domain.release.Track;

import java.util.List;

/**
 * Represents a Discogs release, which contains detailed information
 * about a specific music release including title, artists, formats,
 * genres, and more.
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscogsRelease {

    /**
     * The title of the release.
     */
    private String title;

    /**
     * The unique identifier of the release.
     */
    private int id;

    /**
     * List of main artists involved in the release.
     */
    private List<Artist> artists;

    /**
     * List of extra artists involved in the release. These could be
     * additional performers, producers, or other contributors.
     */
    @JsonProperty("extraartists")
    private List<ExtraArtist> extraArtists;

    /**
     * The number of formats available for the release, such as CD, vinyl, etc.
     */
    @JsonProperty("format_quantity")
    private int formatQuantity;

    /**
     * List of formats in which the release is available.
     */
    private List<Format> formats;

    /**
     * List of genres associated with the release.
     */
    private List<String> genres;

    /**
     * List of labels that published or released this music release.
     */
    private List<Label> labels;

    /**
     * The lowest price available for this release on the marketplace.
     */
    @JsonProperty("lowest_price")
    private double lowestPrice;

    /**
     * The identifier of the master release this release is associated with.
     */
    @JsonProperty("master_id")
    private int masterId;

    /**
     * The URL to the master release this release is associated with.
     */
    @JsonProperty("master_url")
    private String masterUrl;

    /**
     * The release date in its raw format.
     */
    private String released;

    /**
     * The formatted release date.
     */
    @JsonProperty("released_formatted")
    private String releasedFormatted;

    /**
     * The resource URL of this release on the Discogs website.
     */
    @JsonProperty("resource_url")
    private String resourceUrl;

    /**
     * The current status of the release, e.g., 'Official', 'Bootleg', etc.
     */
    private String status;

    /**
     * List of styles (subgenres) associated with the release.
     */
    private List<String> styles;

    /**
     * List of tracks in this release.
     */
    private List<Track> tracklist;

    /**
     * The URI that provides a link to the release page on Discogs.
     */
    private String uri;

    /**
     * The year the release was made available.
     */
    private int year;
}
