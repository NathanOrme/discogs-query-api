package org.discogs.query.service.core;

import java.util.List;
import java.util.Objects;
import org.discogs.query.interfaces.ResultCalculationService;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.stereotype.Service;

/** Service for calculating the size of results from Discogs queries. */
@Service
public class ResultCalculationServiceImpl implements ResultCalculationService {

  @Override
  public int calculateSizeOfResults(final List<DiscogsResultDTO> resultDTOList) {
    return resultDTOList.stream()
        .map(DiscogsResultDTO::results)
        .filter(Objects::nonNull)
        .mapToInt(List::size)
        .sum();
  }
}
