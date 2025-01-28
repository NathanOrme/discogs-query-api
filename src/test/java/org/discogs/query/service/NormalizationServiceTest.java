package org.discogs.query.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.enums.DiscogCountries;
import org.discogs.query.model.enums.DiscogsTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NormalizationServiceTest {

  private NormalizationService normalizationService;

  @BeforeEach
  void setUp() {
    normalizationService = new NormalizationService();
  }

  @Test
  void testNormalizeString() {
    assertNull(normalizationService.normalizeString(null));

    assertEquals("test", normalizationService.normalizeString("test"));
    assertEquals("test", normalizationService.normalizeString("test?"));
    assertEquals("test and example", normalizationService.normalizeString("test & example"));
    assertEquals("test example", normalizationService.normalizeString("test-example"));
    assertEquals("test example", normalizationService.normalizeString("test   example"));
    assertEquals("test example", normalizationService.normalizeString("  test   example  "));
    assertEquals("t est", normalizationService.normalizeString("t ést"));
  }

  @Test
  void testNormalizeQuery() {
    DiscogsQueryDTO query =
        new DiscogsQueryDTO(
            "artist & album",
            "album-title",
            "track-name",
            null,
            "format",
            DiscogCountries.COLOMBIA,
            DiscogsTypes.LABEL,
            "barcode");

    DiscogsQueryDTO normalizedQuery = normalizationService.normalizeQuery(query);

    assertNotNull(normalizedQuery);
    assertEquals("artist and album", normalizedQuery.artist());
    assertEquals("album title", normalizedQuery.album());
    assertEquals("track name", normalizedQuery.track());
    assertEquals("format", normalizedQuery.format());
    assertEquals(DiscogCountries.COLOMBIA, normalizedQuery.country());
    assertEquals(DiscogsTypes.LABEL, normalizedQuery.types());
    assertEquals("barcode", normalizedQuery.barcode());
  }
}
