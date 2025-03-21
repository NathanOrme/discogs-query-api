package org.discogs.query.interfaces;

import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;

/**
 * Service interface for handling Discogs query operations. This interface defines the contract for
 * searching the Discogs database based on a query.
 */
public interface DiscogsQueryService {

  /**
   * Searches the Discogs database based on the provided query.
   *
   * @param discogsQueryDTO the search query data transfer object containing artist, track, and
   *     optional format information
   * @return a {@link DiscogsResultDTO} object containing the search results
   */
  DiscogsResultDTO searchBasedOnQuery(DiscogsQueryDTO discogsQueryDTO);
}
