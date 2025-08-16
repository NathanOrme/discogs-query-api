package org.discogs.query.interfaces;

import java.util.List;
import org.discogs.query.model.DiscogsResultDTO;

/** Interface for calculating the size of results from Discogs queries. */
public interface ResultCalculationService {

  /**
   * Calculates the total number of results across all {@link DiscogsResultDTO} objects.
   *
   * @param resultDTOList the list of {@link DiscogsResultDTO} objects
   * @return the total number of results
   */
  int calculateSizeOfResults(List<DiscogsResultDTO> resultDTOList);
}
