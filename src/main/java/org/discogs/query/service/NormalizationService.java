package org.discogs.query.service;

import lombok.extern.slf4j.Slf4j;
import org.discogs.query.helpers.LogHelper;
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
@Slf4j
@Service
public class NormalizationService {

    /**
     * Normalizes the input string by:
     * - Removing diacritical marks (e.g., accents)
     * - Removing forward and backward slashes and other specific characters as per requirements
     * - Trimming whitespace at the ends
     *
     * @param input the string to be normalized
     * @return the normalized string, or null if the input is null
     */
    public String normalizeString(final String input) {
        if (input == null) {
            LogHelper.warn(() -> "Input is null, returning null as normalized result.");
            return null;
        }

        LogHelper.debug(() -> "Normalizing input: {}", input);

        // Step 1: Remove diacritical marks (accents)
        String noDiacritics = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}", "");

        // Step 2: Perform specific character replacements and removals
        String cleaned = noDiacritics
                .replace(" & ", " and ")
                .replace("'", "")
                .replace("-", " ")
                .replace("?", "")
                .replace("/", "") // Remove forward slashes
                .replace("\\", "") // Remove backward slashes
                .replaceAll("\\s+", " ")
                .replace("*", "")
                .replace("!", "")
                .trim();

        LogHelper.debug(() -> "Normalized result: {}", cleaned);
        return cleaned;
    }

    /**
     * Normalizes the fields of the given {@link DiscogsQueryDTO} object.
     *
     * @param query the {@link DiscogsQueryDTO} object to be normalized; if {@code null}, this method returns {@code
     *              null}
     * @return a new {@link DiscogsQueryDTO} object with normalized fields
     */
    public DiscogsQueryDTO normalizeQuery(final DiscogsQueryDTO query) {
        if (query == null) {
            return null;
        }

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
