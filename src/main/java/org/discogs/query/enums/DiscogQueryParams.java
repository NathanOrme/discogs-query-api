package org.discogs.query.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing different query parameters available for searching in the Discogs API.
 * <p>
 * This enum provides predefined constants for various query parameters, such as track, format, artist, and type.
 * Each constant is associated with a query type string that represents its name in the Discogs API.
 * </p>
 */
@Getter
@AllArgsConstructor
public enum DiscogQueryParams {

    /**
     * Represents the "track" query parameter.
     */
    TRACK("track"),

    /**
     * Represents the "format" query parameter.
     */
    FORMAT("format"),

    /**
     * Represents the "artist" query parameter.
     */
    ARTIST("artist"),

    /**
     * Represents the "type" query parameter.
     */
    TYPE("type");

    /**
     * The query type string associated with the enum constant.
     */
    private final String queryType;
}
