package org.discogs.query.interfaces;

import java.util.List;
import org.discogs.query.model.DiscogsResultDTO;

/** Interface for managing Discogs collection operations and filtering owned releases. */
public interface DiscogsCollectionService {

  /**
   * Filters out all results already owned by the username in their Discogs collection.
   *
   * @param username Username to search against.
   * @param entries List of Discogs search results to filter, using their IDs.
   * @return a filtered list of {@link DiscogsResultDTO} objects without owned releases
   */
  List<DiscogsResultDTO> filterOwnedReleases(String username, List<DiscogsResultDTO> entries);
}
