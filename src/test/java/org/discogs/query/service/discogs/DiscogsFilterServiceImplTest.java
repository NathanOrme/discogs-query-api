package org.discogs.query.service.discogs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import org.discogs.query.domain.api.DiscogsEntry;
import org.discogs.query.domain.api.DiscogsRelease;
import org.discogs.query.domain.api.DiscogsResult;
import org.discogs.query.helpers.DiscogsUrlBuilder;
import org.discogs.query.interfaces.DiscogsAPIClient;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.enums.DiscogCountries;
import org.discogs.query.model.enums.DiscogsTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Unit tests for {@link DiscogsFilterServiceImpl}. */
class DiscogsFilterServiceImplTest {

  @InjectMocks private DiscogsFilterServiceImpl discogsFilterService;

  @Mock private DiscogsAPIClient discogsAPIClient;

  @Mock private DiscogsUrlBuilder discogsUrlBuilder;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Tests retrieving a {@link DiscogsRelease} object based on the provided {@link DiscogsEntry}.
   */
  @Test
  void testGetReleaseDetails() {
    DiscogsEntry entry = new DiscogsEntry();
    entry.setId(123);

    DiscogsRelease release = new DiscogsRelease();
    when(discogsUrlBuilder.buildReleaseUrl(entry)).thenReturn("url");
    when(discogsAPIClient.getRelease("url")).thenReturn(release);

    DiscogsRelease actualRelease = discogsFilterService.getReleaseDetails(entry);
    assertEquals(release, actualRelease);
  }

  /** Tests filtering and sorting the results based on the query DTO. */
  @Test
  void testFilterAndSortResults() {
    DiscogsQueryDTO queryDTO =
        new DiscogsQueryDTO(
            "Artist",
            "Track",
            "Album",
            null,
            "Format",
            DiscogCountries.UK,
            DiscogsTypes.RELEASE,
            "Barcode");

    DiscogsEntry entry = new DiscogsEntry();
    entry.setId(123);
    entry.setLowestPrice(10.0f);

    DiscogsResult result = new DiscogsResult();
    result.setResults(List.of(entry));

    when(discogsAPIClient.getRelease(anyString())).thenReturn(new DiscogsRelease());

    discogsFilterService.filterAndSortResults(queryDTO, result);

    assertTrue(result.getResults().parallelStream().allMatch(e -> e.getLowestPrice() != null));
  }
}
