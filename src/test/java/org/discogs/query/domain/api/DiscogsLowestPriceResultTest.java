package org.discogs.query.domain.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class DiscogsLowestPriceResultTest {

  @Test
  void testDefaultConstructor() {
    DiscogsLowestPriceResult result = new DiscogsLowestPriceResult();
    assertNull(result.getCurrency());
    assertNull(result.getValue());
  }

  @Test
  void testAllArgsConstructor() {
    DiscogsLowestPriceResult result = new DiscogsLowestPriceResult("USD", 25.99f);
    assertEquals("USD", result.getCurrency());
    assertEquals(25.99f, result.getValue());
  }

  @Test
  void testBuilder() {
    DiscogsLowestPriceResult result =
        DiscogsLowestPriceResult.builder().currency("GBP").value(19.99f).build();
    assertEquals("GBP", result.getCurrency());
    assertEquals(19.99f, result.getValue());
  }

  @Test
  void testSetterAndGetter() {
    DiscogsLowestPriceResult result = new DiscogsLowestPriceResult();
    result.setCurrency("EUR");
    result.setValue(29.99f);

    assertEquals("EUR", result.getCurrency());
    assertEquals(29.99f, result.getValue());
  }

  @Test
  void testToString() {
    DiscogsLowestPriceResult result = new DiscogsLowestPriceResult("USD", 25.99f);
    String expectedString = "DiscogsLowestPriceResult(currency=USD, value=25.99)";
    assertEquals(expectedString, result.toString());
  }
}
