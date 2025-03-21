package org.discogs.query.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class ResultCalculationServiceTest {

  @InjectMocks private ResultCalculationService resultCalculationService;

  public ResultCalculationServiceTest() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void calculateSizeOfResults() {
    DiscogsResultDTO resultDTO =
        new DiscogsResultDTO(
            null,
            List.of(new DiscogsEntryDTO(1, null, null, null, null, null, null, null, null, null)));

    List<DiscogsResultDTO> resultDTOList = Collections.singletonList(resultDTO);

    int size = resultCalculationService.calculateSizeOfResults(resultDTOList);

    assertEquals(1, size);
  }
}
