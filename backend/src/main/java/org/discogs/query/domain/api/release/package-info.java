/**
 * This package contains classes that represent various components of a music
 * release
 * in the Discogs database. These components include:
 *
 * <ul>
 *   <li>{@link org.discogs.query.domain.api.release.Artist} -
 *   Represents an artist involved in a release.</li>
 *   <li>{@link org.discogs.query.domain.api.release.ExtraArtist} -
 *   Represents an additional artist or contributor in a release.</li>
 *   <li>{@link org.discogs.query.domain.api.release.Format} -
 *   Represents the format in which the release is available, such as CD or
 *   vinyl.</li>
 *   <li>{@link org.discogs.query.domain.api.release.Label} -
 *   Represents the label responsible for the release.</li>
 *   <li>{@link org.discogs.query.domain.api.release.Track} -
 *   Represents individual tracks in a release, including title, duration,
 *   and position.</li>
 * </ul>
 * <p>
 * These classes are primarily used to map the structure
 * of a music release as stored in the Discogs database,
 * facilitating the retrieval and manipulation of release information.
 * <p>
 * The package is designed to work with JSON
 * serialization/deserialization libraries such as Jackson,
 * making it easier to integrate with APIs that use JSON data formats.
 */
package org.discogs.query.domain.api.release;
