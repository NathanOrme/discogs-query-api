package org.discogs.query.interfaces;

import org.discogs.query.model.DiscogsQueryDTO;

/**
 * Interface for normalizing strings by performing various transformations such as removing
 * diacritical marks, replacing special characters, and trimming whitespace.
 */
public interface NormalizationService {

  /**
   * Normalizes the input string by removing diacritical marks, replacing special characters, and
   * trimming whitespace.
   *
   * @param input the string to be normalized
   * @return the normalized string, or null if the input is null
   */
  String normalizeString(String input);

  /**
   * Normalizes the fields of the given {@link DiscogsQueryDTO} object.
   *
   * @param query the {@link DiscogsQueryDTO} object to be normalized; if {@code null}, this method
   *     returns {@code null}
   * @return a new {@link DiscogsQueryDTO} object with normalized fields
   */
  DiscogsQueryDTO normalizeQuery(DiscogsQueryDTO query);
}