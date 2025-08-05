package org.discogs.query.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.discogs.query.interfaces.BatchMarketplaceService;
import org.discogs.query.interfaces.DiscogsQueryService;
import org.discogs.query.interfaces.DiscogsWebScraperClient;
import org.discogs.query.service.discogs.DiscogsCollectionService;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsRequestDTO;
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

@ExtendWith(MockitoExtension.class)
class QueryProcessingServiceTest {

  @Mock private DiscogsQueryService discogsQueryService;

  @Mock private NormalizationService normalizationService;

  @Mock private DiscogsWebScraperClient discogsWebScraperClient;

  @Mock private DiscogsCollectionService discogsCollectionService;

  @Mock private BatchMarketplaceService batchMarketplaceService;

  @Mock private AsyncQueryService asyncQueryService;

  @InjectMocks private QueryProcessingService queryProcessingService;

  private DiscogsQueryDTO queryDTO;

  @BeforeEach
  void setUp() {
    queryDTO =
        new DiscogsQueryDTO(
            "Artist Name",
            "Album Name",
            "Track Name",
            "Title",
            DiscogsFormats.VINYL.getFormat(),
            DiscogCountries.UK,
            DiscogsTypes.LABEL,
            "123456");
  }

  @Test
  void processQueries_ShouldProcessEachQueryAndReturnResults() {
    // Mock normalizing queries
    when(normalizationService.normalizeQuery(any())).thenReturn(queryDTO);
    
    // Mock search results for each query
    DiscogsResultDTO resultDTO =
        new DiscogsResultDTO(
            queryDTO,
            List.of(
                new DiscogsEntryDTO(
                    1, "Title", List.of("Vinyl"), "url", "uri", "UK", "2023", true, 10.0f, 5)));
    
    // Mock the async service to return completed futures
    when(asyncQueryService.createOptimizedFutures(any())).thenReturn(
        List.of(CompletableFuture.completedFuture(resultDTO)));

    List<DiscogsQueryDTO> queryDTOList = List.of(queryDTO);
    DiscogsRequestDTO discogsRequestDTO = new DiscogsRequestDTO(queryDTOList, null);
    List<DiscogsResultDTO> results = queryProcessingService.processQueries(discogsRequestDTO, 5);

    assertFalse(results.isEmpty());
    verify(asyncQueryService, times(1)).createOptimizedFutures(any());
  }
}
