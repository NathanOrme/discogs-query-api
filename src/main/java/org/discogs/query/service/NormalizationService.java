package org.discogs.query.service;

import org.discogs.query.model.DiscogsQueryDTO;
import org.springframework.stereotype.Service;

import java.text.Normalizer;

/**
 * Service for normalizing strings by performing the following transformations:
 * <ul>
 *     <li>Removing diacritical marks (accents, etc.) from characters.</li>
 *     <li>Replacing occurrences of " and " with " ampersand".</li>
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
                .replace("?", "")
                .replaceAll("\\s+", " ")
                .replace("*", "")
                .replace("!", "")
                .trim();
    }

    /**
     * Normalizes the fields of the given {@link DiscogsQueryDTO} object.
     *
     * @param query the {@link DiscogsQueryDTO} object to be normalized; if {@code null}, this method returns {@code
     *              null}
     * @return a new {@link DiscogsQueryDTO} object with normalized fields
     */
    public DiscogsQueryDTO normalizeQuery(final DiscogsQueryDTO query) {
        if (query == null) return null;

        return new DiscogsQueryDTO(
                normalizeString(query.artist()),
                normalizeString(query.album()),
                normalizeString(query.track()),
                normalizeString(query.title()),
                query.format(),
                query.country(),
                query.types(),
                query.barcode()
        );
    }
}
