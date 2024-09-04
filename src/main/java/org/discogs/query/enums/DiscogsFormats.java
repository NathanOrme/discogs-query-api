package org.discogs.query.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing different formats available in the Discogs API.
 * <p>
 * This enum provides predefined constants for different music formats,
 * such as Vinyl, Compilation, and Vinyl Compilation.
 * Each constant is associated with a format string
 * that represents its name in the Discogs API.
 */
@Getter
@AllArgsConstructor
public enum DiscogsFormats {

    /**
     * Represents the "lp" format.
     */
    LP("lp"),

    /**
     * Represents the "album" format.
     */
    ALBUM("album"),

    /**
     * Represents the "vinyl" format.
     */
    VINYL("vinyl"),

    /**
     * Represents the "compilation" format.
     */
    COMP("compilation"),

    /**
     * Represents the "compilation vinyl" format.
     */
    VINYL_COMPILATION("compilation vinyl"),

    /**
     * Represents the "compilation vinyl" format.
     */
    VINYL_ALBUM("album vinyl"),

    /**
     * Represents an unknown or unspecified type.
     * <p>
     * This type is used when the input type string does not match any
     * defined types.
     */
    UNKNOWN("");

    /**
     * The format string associated with the enum constant.
     */
    private final String format;

    /**
     * Returns the {@link DiscogsFormats} constant associated with the given
     * format string.
     * <p>
     * If the format string does not match any defined constant,
     * {@link #UNKNOWN} is returned.
     *
     * @param format the format string to match
     * @return the {@link DiscogsFormats} constant corresponding to the
     * format string,
     * or {@link #UNKNOWN} if no match is found
     */
    public static DiscogsFormats fromString(final String format) {
        for (final DiscogsFormats t : DiscogsFormats.values()) {
            if (t.getFormat().equalsIgnoreCase(format)) {
                return t;
            }
        }
        return UNKNOWN;
    }

}