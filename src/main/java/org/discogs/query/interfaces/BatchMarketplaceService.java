package org.discogs.query.interfaces;

import java.util.List;
import java.util.Set;
import org.discogs.query.model.DiscogsEntryDTO;

/**
 * Service for batch processing marketplace checks to optimize API calls.
 * This service replaces N+1 individual marketplace calls with efficient batch operations.
 */
public interface BatchMarketplaceService {

  /**
   * Filters entries that have UK marketplace listings using batch processing.
   * This method reduces N individual API calls to a single batch operation.
   *
   * @param entries the list of entries to check for UK marketplace availability
   * @return a filtered list containing only entries with UK marketplace listings
   */
  List<DiscogsEntryDTO> filterEntriesWithUKMarketplace(List<DiscogsEntryDTO> entries);

  /**
   * Retrieves marketplace availability for multiple release IDs in a batch.
   * Deduplicates release IDs to avoid redundant API calls.
   *
   * @param releaseIds the set of unique release IDs to check
   * @return a set of release IDs that have UK marketplace listings
   */
  Set<Integer> batchCheckMarketplaceAvailability(Set<Integer> releaseIds);
}