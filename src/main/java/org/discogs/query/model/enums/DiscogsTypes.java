package org.discogs.query.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing different types of resources available in the Discogs API.
 * <p>
 * This enum provides predefined constants for various resource types, such
 * as releases, masters, artists, and labels.
 * Each constant is associated with a type string that represents its name in
 * the Discogs API.
 * <p>
 * The available types are:
 * <ul>
 *     <li>{@link #RELEASE} - Represents a "release" resource type.</li>
 *     <li>{@link #MASTER} - Represents a "master" resource type.</li>
 *     <li>{@link #ARTIST} - Represents an "artist" resource type.</li>
 *     <li>{@link #LABEL} - Represents a "label" resource type.</li>
 *     <li>{@link #UNKNOWN} - Represents an unknown or unspecified type.</li>
 * </ul>
 * <p>
 * The {@link #UNKNOWN} type is used as a default when
 * the provided type string does not match any of the defined constants.
 */
@Getter
@RequiredArgsConstructor
public enum DiscogsTypes {

    /**
     * Represents a "release" resource type in the Discogs API.
     * <p>
     * This is the default type used if no specific type is provided.
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
    LABEL("label"),

    /**
     * Represents an unknown or unspecified type.
     * <p>
     * This type is used when the input type string does not match any
     * defined types.
     */
    UNKNOWN("");

    /**
     * The type string associated with the enum constant.
     */
    private final String type;

    /**
     * Returns the {@link DiscogsTypes} constant associated with the given
     * type string.
     * <p>
     * If the type string does not match any defined constant,
     * {@link #UNKNOWN} is returned.
     *
     * @param type the type string to match
     * @return the {@link DiscogsTypes} constant corresponding to the type
     * string,
     * or {@link #UNKNOWN} if no match is found
     */
    public static DiscogsTypes fromString(final String type) {
        for (final DiscogsTypes t : DiscogsTypes.values()) {
            if (t.getType().equalsIgnoreCase(type)) {
                return t;
            }
        }
        return UNKNOWN;
    }
}
