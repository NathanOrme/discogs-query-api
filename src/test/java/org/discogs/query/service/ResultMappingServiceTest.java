package org.discogs.query.service;

import org.discogs.query.interfaces.DiscogsMappingService;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ResultMappingServiceTest {

    @Mock
    private DiscogsMappingService discogsMappingService;

    @InjectMocks
    private ResultMappingService resultMappingService;

    public ResultMappingServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void mapResultsToDTO() {
        DiscogsResultDTO resultDTO = new DiscogsResultDTO();
        DiscogsMapResultDTO mapResultDTO = new DiscogsMapResultDTO();

        when(discogsMappingService.convertEntriesToMapByTitle(resultDTO)).thenReturn(mapResultDTO);

        List<DiscogsMapResultDTO> results = resultMappingService.mapResultsToDTO(
                Collections.singletonList(resultDTO));

        assertEquals(1, results.size());
        assertEquals(mapResultDTO, results.get(0));
    }
}
