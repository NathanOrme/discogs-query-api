package org.discogs.query;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DiscogsApplicationTest {

  @Test
  void main_WithDefaultArgs_ThrowsNoException() {
    assertDoesNotThrow(() -> DiscogsApplication.main(new String[0]));
  }
}
