package org.discogs.query.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing different formats available in the Discogs API.
 * <p>
 * This enum provides predefined constants for different music formats, such as Vinyl, Compilation, and Vinyl Compilation.
 * Each constant is associated with a format string that represents its name in the Discogs API.
 * </p>
 */
@Getter
@AllArgsConstructor
public enum DiscogFormats {

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
    VINYL_COMPILATION("compilation vinyl");

    /**
     * The format string associated with the enum constant.
     */
    private final String format;
}