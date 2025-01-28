package org.discogs.query.model.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DiscogQueryParamsTest {

  @Test
  void testEnumValues() {
    // Arrange
    DiscogQueryParams track = DiscogQueryParams.TRACK;
    DiscogQueryParams format = DiscogQueryParams.FORMAT;
    DiscogQueryParams artist = DiscogQueryParams.ARTIST;
    DiscogQueryParams type = DiscogQueryParams.TYPE;

    // Act & Assert
    assertEquals("track", track.getQueryType(), "The queryType for TRACK " + "should be 'track'");
    assertEquals(
        "format", format.getQueryType(), "The queryType for " + "FORMAT should be 'format'");
    assertEquals(
        "artist", artist.getQueryType(), "The queryType for " + "ARTIST should be 'artist'");
    assertEquals("type", type.getQueryType(), "The queryType for TYPE " + "should be 'type'");
  }
}
