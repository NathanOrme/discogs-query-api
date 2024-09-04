package org.discogs.query.domain.release;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an artist in the context of a release.
 * This class is used to hold information about an artist, including their
 * identifier, name, role, and associated details within a release.
 *
 * <p>Uses Lombok annotations to generate boilerplate code such as getters,
 * setters,
 * and constructors automatically.</p>
 *
 * <p>Annotations:</p>
 * <ul>
 *     <li>{@link Data} - Lombok annotation to generate getters, setters,
 *     equals, hashCode, and toString methods.</li>
 *     <li>{@link NoArgsConstructor} - Lombok annotation to generate a
 *     no-arguments constructor.</li>
 *     <li>{@link JsonProperty} - Jackson annotation to map the JSON property
 *     to the Java field.</li>
 * </ul>
 */
@Data
@NoArgsConstructor
public class Artist {

    /**
     * The artist's name as it appears in the release's track listing.
     * This field represents the artist's name in the "anv" (also known as
     * "artist name variation") field.
     */
    private String anv;

    /**
     * The unique identifier for the artist.
     * This is typically an integer value that uniquely identifies the artist
     * within the system.
     */
    private int id;

    /**
     * The role of the artist in the release.
     * This field describes the artist's contribution or role, such as
     * "producer" or "vocalist".
     */
    private String join;

    /**
     * The name of the artist.
     * This field represents the actual name of the artist as it appears in
     * the release.
     */
    private String name;

    /**
     * The URL to the resource related to the artist.
     * This is a URL that provides additional information about the artist.
     * It is mapped from the JSON property "resource_url".
     */
    @JsonProperty("resource_url")
    private String resourceUrl;

    /**
     * The role of the artist in the release.
     * This field indicates the specific role or function the artist performed,
     * such as "performer", "composer", etc.
     */
    private String role;

    /**
     * The list of tracks associated with the artist.
     * This field contains the tracks on which the artist performed or
     * contributed.
     */
    private String tracks;
}
