package org.discogs.query.domain.api.release;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an extra artist involved in a release. These could be additional performers,
 * producers, or other contributors not considered the main artist(s).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtraArtist {

    /**
     * The artist name variation (ANV) used on the release. This could be an alternate spelling,
     * alias, or pseudonym.
     */
    private String anv;

    /**
     * The unique identifier of the extra artist.
     */
    private int id;

    /**
     * The joining phrase or term, if the artist is part of a group (e.g., "feat.", "vs", etc.).
     */
    private String join;

    /**
     * The name of the extra artist as it appears on the release.
     */
    private String name;

    /**
     * The resource URL pointing to the extra artist's page on Discogs.
     */
    @JsonProperty("resource_url")
    private String resourceUrl;

    /**
     * The role of the extra artist in the release (e.g., "Vocals", "Producer", etc.).
     */
    private String role;

    /**
     * The specific tracks the extra artist is credited on, if applicable.
     */
    private String tracks;
}
