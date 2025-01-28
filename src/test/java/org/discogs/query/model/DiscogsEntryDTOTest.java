package org.discogs.query.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class DiscogsEntryDTOTest {

  @Test
  void testDtoCreation() {
    // Arrange
    DiscogsEntryDTO dto =
        new DiscogsEntryDTO(
            1,
            "Sample Title",
            Arrays.asList("vinyl", "CD"),
            "http://example.com/master",
            "http://example.com/entry",
            null, // country
            null, // year
            null, // isOnMarketplace
            null, // lowestPrice
            null // numberForSale
            );

    // Act & Assert
    assertNotNull(dto);
    assertEquals(1, dto.id());
    assertEquals("Sample Title", dto.title());
    assertEquals(Arrays.asList("vinyl", "CD"), dto.format());
    assertEquals("http://example.com/master", dto.url());
    assertEquals("http://example.com/entry", dto.uri());
  }
}
