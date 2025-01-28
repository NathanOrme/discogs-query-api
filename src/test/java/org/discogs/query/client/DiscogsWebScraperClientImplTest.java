package org.discogs.query.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import org.discogs.query.config.HttpConfig;
import org.discogs.query.domain.website.DiscogsWebsiteResult;
import org.discogs.query.exceptions.NoMarketplaceListingsException;
import org.discogs.query.helpers.JsoupHelper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

@ExtendWith(MockitoExtension.class)
class DiscogsWebScraperClientImplTest {

  @Mock private HttpConfig httpConfig;

  @Mock private JsoupHelper jsoupHelper;

  @InjectMocks private DiscogsWebScraperClientImpl discogsWebScraperClient;

  private final String releaseId = "123456";

  @BeforeEach
  void setUp() {
    when(httpConfig.buildHeaders()).thenReturn(new HttpHeaders());
  }

  @Test
  @Disabled
  void testGetMarketplaceResultsForRelease_Success() throws IOException {
    // Arrange
    Document mockDocument = mock(Document.class);
    Elements mockListings = mock(Elements.class);
    Element mockListing = mock(Element.class);

    when(mockListings.isEmpty()).thenReturn(false);
    when(mockListings.getFirst()).thenReturn(mockListing);
    when(jsoupHelper.connect(anyString(), anyMap())).thenReturn(mockDocument);
    when(mockDocument.select(".shortcut_navigable")).thenReturn(mockListings);
    when(mockListings.select("ul > li")).thenReturn(mockListings); // Simulating seller items

    // Act
    List<DiscogsWebsiteResult> results =
        discogsWebScraperClient.getMarketplaceResultsForRelease(releaseId);

    // Assert
    assertNotNull(results);
    assertFalse(results.isEmpty());
    verify(jsoupHelper).connect(anyString(), anyMap());
  }

  @Test
  void testGetMarketplaceResultsForRelease_NoListings() throws IOException {
    // Arrange
    Document mockDocument = mock(Document.class);
    Elements mockListings = new Elements(); // Empty listings
    when(jsoupHelper.connect(anyString(), anyMap())).thenReturn(mockDocument);
    when(mockDocument.select(".shortcut_navigable")).thenReturn(mockListings);

    // Act
    List<DiscogsWebsiteResult> results =
        discogsWebScraperClient.getMarketplaceResultsForRelease(releaseId);

    // Assert
    assertNotNull(results);
    assertTrue(results.isEmpty());
    verify(jsoupHelper).connect(anyString(), anyMap());
  }

  @Test
  void testGetMarketplaceResultsForRelease_ExceptionHandling() throws IOException {
    // Arrange
    when(jsoupHelper.connect(anyString(), anyMap()))
        .thenThrow(new RuntimeException("Network error"));

    // Act & Assert
    NoMarketplaceListingsException thrown =
        assertThrows(
            NoMarketplaceListingsException.class,
            () -> {
              discogsWebScraperClient.getMarketplaceResultsForRelease(releaseId);
            });

    assertEquals(
        "Failed to scrape data from Discogs Marketplace after 3 attempts", thrown.getMessage());
    verify(jsoupHelper, times(3)).connect(anyString(), anyMap()); // Ensure it retried 3 times
  }
}
