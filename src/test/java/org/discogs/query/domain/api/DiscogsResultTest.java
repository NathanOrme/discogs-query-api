package org.discogs.query.domain.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import org.junit.jupiter.api.Test;

class DiscogsResultTest {

  @Test
  void testDtoCreation() {
    // Arrange
    DiscogsEntry entry =
        DiscogsEntry.builder()
            .id(1)
            .title("Sample Title")
            .format(Collections.singletonList("vinyl"))
            .url("http://example.com/master")
            .uri("http://example.com/entry")
            .build();

    DiscogsResult resultDTO =
        DiscogsResult.builder().results(Collections.singletonList(entry)).build();

    // Act & Assert
    assertNotNull(resultDTO);
    assertNotNull(resultDTO.getResults());
    assertEquals(1, resultDTO.getResults().size());
    assertEquals(entry, resultDTO.getResults().get(0));
  }
}
