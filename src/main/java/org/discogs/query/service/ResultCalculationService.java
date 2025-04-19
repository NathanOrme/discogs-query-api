package org.discogs.query.service;

import java.util.List;
import java.util.Objects;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.stereotype.Service;

/** Service for calculating the size of results from Discogs queries. */
@Service
public class ResultCalculationService {

  /**
   * Calculates the total number of results across all {@link DiscogsResultDTO} objects.
   *
   * @param resultDTOList the list of {@link DiscogsResultDTO} objects
   * @return the total number of results
   */
  public int calculateSizeOfResults(final List<DiscogsResultDTO> resultDTOList) {
    return resultDTOList.stream()
        .map(DiscogsResultDTO::results)
        .filter(Objects::nonNull)
        .mapToInt(List::size)
        .sum();
  }
}
