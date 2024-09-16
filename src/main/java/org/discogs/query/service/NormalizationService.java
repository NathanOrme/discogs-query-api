package org.discogs.query.service;

import org.springframework.stereotype.Service;

import java.text.Normalizer;

/**
 * Service for normalizing strings by performing the following transformations:
 * <ul>
 *     <li>Removing diacritical marks (accents, etc.) from characters.</li>
 *     <li>Replacing occurrences of " and " with " &".</li>
 *     <li>Removing apostrophes.</li>
 *     <li>Replacing hyphens with spaces.</li>
 *     <li>Replacing multiple whitespace characters with a single space.</li>
 *     <li>Trimming leading and trailing whitespace.</li>
 * </ul>
 */
@Service
public class NormalizationService {

    /**
     * Normalizes the input string based on the transformations defined in this service.
     *
     * @param input the string to be normalized; if {@code null}, this method returns {@code null}
     * @return the normalized string
     */
    public String normalizeString(final String input) {
        if (input == null) return null;

        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}", "")
                .replace(" & ", " and ")
                .replace("'", "")
                .replace("-", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
