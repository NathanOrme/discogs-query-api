package org.discogs.query.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing different types of resources available in the Discogs API.
 * <p>
 * This enum provides predefined constants for various resource types, such as releases, masters, artists, and labels.
 * Each constant is associated with a type string that represents its name in the Discogs API.
 * </p>
 */
@Getter
@AllArgsConstructor
public enum DiscogsTypes {

    /**
     * Represents a "release" resource type in the Discogs API.
     */
    RELEASE("release"),

    /**
     * Represents a "master" resource type in the Discogs API.
     */
    MASTER("master"),

    /**
     * Represents an "artist" resource type in the Discogs API.
     */
    ARTIST("artist"),

    /**
     * Represents a "label" resource type in the Discogs API.
     */
    LABEL("label");

    /**
     * The type string associated with the enum constant.
     */
    private final String type;
}
