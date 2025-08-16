package org.discogs.query.service.util;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.helpers.LogHelper;
import org.discogs.query.interfaces.DeduplicationService;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.springframework.stereotype.Service;

/**
 * Service responsible for removing duplicate entries from Discogs search results. Follows Single
 * Responsibility Principle by handling only deduplication logic.
 */
@Slf4j
@Service
public class DeduplicationServiceImpl implements DeduplicationService {

  @Override
  public List<DiscogsMapResultDTO> filterDuplicateEntries(
      final List<DiscogsMapResultDTO> discogsMapResultDTOS) {
    LogHelper.debug(
        () -> "Starting deduplication process for {} result sets", discogsMapResultDTOS.size());

    int totalEntriesBeforeDedup = countTotalEntries(discogsMapResultDTOS);

    discogsMapResultDTOS.forEach(this::removeDuplicatesFromResultSet);

    int totalEntriesAfterDedup = countTotalEntries(discogsMapResultDTOS);
    int duplicatesRemoved = totalEntriesBeforeDedup - totalEntriesAfterDedup;

    LogHelper.info(
        () -> "Deduplication complete. Removed {} duplicate entries from {} total entries",
        duplicatesRemoved,
        totalEntriesBeforeDedup);

    return discogsMapResultDTOS;
  }

  /**
   * Removes duplicate entries from a single DiscogsMapResultDTO.
   *
   * @param discogsMapResultDTO the result set to deduplicate
   */
  private void removeDuplicatesFromResultSet(final DiscogsMapResultDTO discogsMapResultDTO) {
    for (final Map.Entry<String, List<DiscogsEntryDTO>> resultEntry :
        discogsMapResultDTO.results().entrySet()) {
      List<DiscogsEntryDTO> deduplicatedEntries = removeDuplicateEntries(resultEntry.getValue());
      resultEntry.setValue(deduplicatedEntries);
    }
  }

  /**
   * Removes duplicate entries from a list of DiscogsEntryDTO based on their IDs. Uses a HashSet for
   * O(1) lookup performance during deduplication.
   *
   * @param entries the list of DiscogsEntryDTO to filter
   * @return a new list of unique DiscogsEntryDTO without duplicates
   */
  private List<DiscogsEntryDTO> removeDuplicateEntries(final List<DiscogsEntryDTO> entries) {
    if (entries == null || entries.isEmpty()) {
      return entries;
    }

    Set<Integer> seenIds = new HashSet<>();
    return entries.stream().filter(entry -> entry != null && seenIds.add(entry.id())).toList();
  }

  /**
   * Counts the total number of entries across all result sets. Useful for monitoring and logging
   * deduplication metrics.
   *
   * @param discogsMapResultDTOS the list of result sets
   * @return total number of entries
   */
  private int countTotalEntries(final List<DiscogsMapResultDTO> discogsMapResultDTOS) {
    return discogsMapResultDTOS.stream()
        .mapToInt(resultDTO -> resultDTO.results().values().stream().mapToInt(List::size).sum())
        .sum();
  }
}