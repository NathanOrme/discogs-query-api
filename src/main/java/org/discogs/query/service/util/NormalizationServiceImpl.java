package org.discogs.query.service.util;

import java.text.Normalizer;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.interfaces.NormalizationService;
import org.discogs.query.model.DiscogsQueryDTO;
import org.springframework.stereotype.Service;

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
public class NormalizationServiceImpl implements NormalizationService {

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

  private static final Pattern DIACRITICAL_PATTERN =
      Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
  private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

  @Override
  public String normalizeString(final String input) {
    if (input == null) {
      return null;
    }
    // Step 1: Remove diacritical marks (accents)
    String cleaned = Normalizer.normalize(input, Normalizer.Form.NFD);
    cleaned = DIACRITICAL_PATTERN.matcher(cleaned).replaceAll("");
    // Step 2: Perform specific character replacements and removals
    for (final Map.Entry<String, String> entry : replacements.entrySet()) {
      cleaned = cleaned.replace(entry.getKey(), entry.getValue());
    }
    cleaned = WHITESPACE_PATTERN.matcher(cleaned).replaceAll(" ").trim();
    return cleaned;
  }

  @Override
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
