package org.discogs.query.service;

import org.springframework.stereotype.Service;

import java.text.Normalizer;

/**
 * Service for normalizing strings by removing diacritical marks, replacing common characters,
 * and standardizing whitespace.
 */
@Service
public class NormalizationService {

    public String normalizeString(final String input) {
        if (input == null) return null;

        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}", "")
                .replace(" and ", " &")
                .replace("'", "")
                .replace("-", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
