package org.discogs.query.service;

import lombok.extern.slf4j.Slf4j;
import org.discogs.query.model.DiscogsQueryDTO;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Map;

/**
 * Service for normalizing strings by performing the following transformations:
 *
 * <ul>
 *   <li>Removing diacritical marks (accents, etc.) from characters.
 *   <li>Replacing occurrences of " & " with " and ".
 *   <li>Removing apostrophes.
 *   <li>Replacing hyphens with spaces.
 *   <li>Replacing multiple whitespace characters with a single space.
 *   <li>Trimming leading and trailing whitespace.
 * </ul>
 */
@Slf4j
@Service
public class NormalizationService {

    private static final Map<String, String> replacements =
            Map.of(
                    " & ", " and ",
                    "'", "",
                    "-", " ",
                    "?", "",
                    "/", " ",
                    "\\", " ",
                    "*", "",
                    "!", "");

    /**
     * Normalizes the input string by: - Removing diacritical marks (e.g., accents) - Removing forward
     * and backward slashes and other specific characters as per requirements - Trimming whitespace at
     * the ends
     *
     * @param input the string to be normalized
     * @return the normalized string, or null if the input is null
     */
    public String normalizeString(final String input) {
        if (input == null) {
            return null;
        }
        // Step 1: Remove diacritical marks (accents)
        String cleaned =
                Normalizer.normalize(input, Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}", "");
        // Step 2: Perform specific character replacements and removals
        for (final Map.Entry<String, String> entry : replacements.entrySet()) {
            cleaned = cleaned.replace(entry.getKey(), entry.getValue());
        }
        cleaned = cleaned.replaceAll("\\s+", " ").trim();
        return cleaned;
    }

    /**
     * Normalizes the fields of the given {@link DiscogsQueryDTO} object.
     *
     * @param query the {@link DiscogsQueryDTO} object to be normalized; if {@code null}, this method
     *              returns {@code null}
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
                query.barcode());
    }
}
