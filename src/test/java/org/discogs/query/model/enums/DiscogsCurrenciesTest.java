package org.discogs.query.model.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DiscogsCurrenciesTest {

  @Test
  void testCurrencyValues() {
    assertEquals("gbp", DiscogsCurrencies.GBP.getCurrency());
    assertEquals("usd", DiscogsCurrencies.USD.getCurrency());
  }
}
