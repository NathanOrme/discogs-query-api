package org.discogs.query.interfaces;

import java.util.List;
import org.discogs.query.model.DiscogsRequestDTO;
import org.discogs.query.model.DiscogsResultDTO;

/**
 * Interface for processing Discogs queries using asynchronous tasks. This service handles query
 * processing, result filtering, and timeout management.
 */
public interface QueryProcessingService {

  /**
   * Processes each Discogs query asynchronously, normalizes the queries, and retrieves the results
   * from the Discogs API.
   *
   * @param discogsRequestDTO the {@link DiscogsRequestDTO} objects to process
   * @param timeoutInSeconds the timeout in seconds for each query to be processed
   * @return a list of {@link DiscogsResultDTO} objects containing the query results
   */
  List<DiscogsResultDTO> processQueries(DiscogsRequestDTO discogsRequestDTO, long timeoutInSeconds);

  /**
   * Filters out Discogs entries that are not shipping from the UK marketplace.
   *
   * @param results the list of {@link DiscogsResultDTO} objects containing search results
   * @return a filtered list of {@link DiscogsResultDTO} objects where only UK-shipping entries
   *     remain
   */
  List<DiscogsResultDTO> filterOutEntriesNotShippingFromUk(List<DiscogsResultDTO> results);
}
