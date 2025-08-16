package org.discogs.query.interfaces;

import java.util.List;
import org.discogs.query.model.DiscogsMapResultDTO;

/**
 * Interface for removing duplicate entries from Discogs search results. Follows Single
 * Responsibility Principle by handling only deduplication logic.
 */
public interface DeduplicationService {

  /**
   * Filters duplicate entries from a list of DiscogsMapResultDTO. This method mutates the input
   * list by removing duplicates in-place.
   *
   * @param discogsMapResultDTOS the list of DiscogsMapResultDTO to filter
   * @return the same list with duplicates removed for method chaining
   */
  List<DiscogsMapResultDTO> filterDuplicateEntries(List<DiscogsMapResultDTO> discogsMapResultDTOS);
}