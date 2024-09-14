package org.discogs.query.service;

import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResultCalculationServiceTest {

    @InjectMocks
    private ResultCalculationService resultCalculationService;

    public ResultCalculationServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void calculateSizeOfResults() {
        DiscogsResultDTO resultDTO = new DiscogsResultDTO();
        resultDTO.setResults(Collections.singletonList(new DiscogsEntryDTO()));

        List<DiscogsResultDTO> resultDTOList = Collections.singletonList(resultDTO);

        int size = resultCalculationService.calculateSizeOfResults(resultDTOList);

        assertEquals(1, size);
    }
}
