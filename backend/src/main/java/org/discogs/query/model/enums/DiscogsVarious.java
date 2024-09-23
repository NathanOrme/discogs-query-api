package org.discogs.query.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing various artist-related strings in the Discogs database.
 * This enum contains constants for common variations of the term "various
 * artists."
 */
@Getter
@AllArgsConstructor
public enum DiscogsVarious {

    /**
     * Represents the string "various."
     */
    VARIOUS("various"),

    /**
     * Represents the string "various artists."
     */
    VARIOUS_ARTIST("various artists");

    /**
     * The name associated with the various artist string.
     */
    private final String variousName;
}
