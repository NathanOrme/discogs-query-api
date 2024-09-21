package org.discogs.query.service;

import org.discogs.query.interfaces.DiscogsQueryService;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class QueryProcessingServiceTest {

    @Mock
    private DiscogsQueryService discogsQueryService;

    @Mock
    private NormalizationService normalizationService;

    @InjectMocks
    private QueryProcessingService queryProcessingService;

    public QueryProcessingServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processQueries_Success() {
        DiscogsQueryDTO queryDTO = new DiscogsQueryDTO(null, null, null, null,
                null, null, null);
        DiscogsResultDTO resultDTO = new DiscogsResultDTO(null, null);

        when(discogsQueryService.searchBasedOnQuery(any())).thenReturn(resultDTO);
        when(normalizationService.normalizeString(anyString())).thenReturn(null);

        List<DiscogsResultDTO> results = queryProcessingService.processQueries(
                Collections.singletonList(queryDTO), 5);

        assertEquals(1, results.size());
        assertEquals(resultDTO, results.get(0));
    }
}
