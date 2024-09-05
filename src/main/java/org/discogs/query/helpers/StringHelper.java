package org.discogs.query.helpers;

import lombok.NoArgsConstructor;
import org.discogs.query.enums.DiscogsVarious;
import org.springframework.stereotype.Component;

/**
 * A utility class that provides helper methods for
 * String manipulation and validation.
 * This class is annotated as a Spring Component,
 * allowing it to be managed by Spring's
 * dependency injection framework.
 *
 * <p>
 * The primary function of this class is to check if a given string
 * is neither null nor blank.
 * <p>
 * Example usage:
 * <pre>
 *     StringHelper stringHelper = new StringHelper();
 *     boolean result = stringHelper.isNotNullOrBlank("example");
 * </pre>
 */
@Component
@NoArgsConstructor
public class StringHelper {

    /**
     * Checks if the given string is not null and not blank.
     *
     * <p>
     * A string is considered not blank if it
     * contains at least one non-whitespace character.
     *
     * @param string the string to check
     * @return {@code true} if the string is not null and contains at
     * least one non-whitespace character,
     * {@code false} otherwise
     */
    public boolean isNotNullOrBlank(final String string) {
        return string != null && !string.isBlank();
    }

    /**
     * Checks if the artist is not categorized as a "Various Artists" entry.
     *
     * @param artist the artist name to check.
     * @return {@code true} if the artist is not categorized as "Various
     * Artists", otherwise {@code false}.
     */
    public boolean isNotVariousArtist(final String artist) {
        return !DiscogsVarious.VARIOUS.getVariousName().equalsIgnoreCase(artist)
                && !DiscogsVarious.VARIOUS_ARTIST.getVariousName().equalsIgnoreCase(artist);
    }
}
