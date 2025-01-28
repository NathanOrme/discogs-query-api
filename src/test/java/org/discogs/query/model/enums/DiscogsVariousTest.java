package org.discogs.query.model.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DiscogsVariousTest {

  @Test
  void testVariousValues() {
    assertEquals("various", DiscogsVarious.VARIOUS.getVariousName());
    assertEquals("various artists", DiscogsVarious.VARIOUS_ARTIST.getVariousName());
  }
}
