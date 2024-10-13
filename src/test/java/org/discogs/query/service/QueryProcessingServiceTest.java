package org.discogs.query.service;

import org.discogs.query.interfaces.DiscogsQueryService;
import org.discogs.query.interfaces.DiscogsWebScraperClient;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.discogs.query.model.enums.DiscogCountries;
import org.discogs.query.model.enums.DiscogsFormats;
import org.discogs.query.model.enums.DiscogsTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueryProcessingServiceTest {

    @Mock
    private DiscogsQueryService discogsQueryService;

    @Mock
    private NormalizationService normalizationService;

    @Mock
    private DiscogsWebScraperClient discogsWebScraperClient;

    @InjectMocks
    private QueryProcessingService queryProcessingService;

    private DiscogsQueryDTO queryDTO;

    @BeforeEach
    void setUp() {
        queryDTO = new DiscogsQueryDTO(
                "Artist Name", "Album Name", "Track Name", "Title",
                DiscogsFormats.VINYL.getFormat(), DiscogCountries.UK, DiscogsTypes.LABEL, "123456"
        );
    }

    @Test
    void processQueries_ShouldProcessEachQueryAndReturnResults() {
        // Mock normalizing queries
        when(normalizationService.normalizeQuery(any())).thenReturn(queryDTO);
        // Mock search results for each query
        DiscogsResultDTO resultDTO = new DiscogsResultDTO(queryDTO, List.of(new DiscogsEntryDTO(1, "Title", List.of(
                "Vinyl"), "url", "uri", "UK", "2023", true, 10.0f, 5)));
        when(discogsQueryService.searchBasedOnQuery(any())).thenReturn(resultDTO);

        List<DiscogsQueryDTO> queryDTOList = List.of(queryDTO);
        List<DiscogsResultDTO> results = queryProcessingService.processQueries(queryDTOList, 5);

        assertFalse(results.isEmpty());
        verify(discogsQueryService, times(1)).searchBasedOnQuery(any());
    }


}
