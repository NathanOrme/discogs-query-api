package org.discogs.query.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscogsCurrenciesTest {

    @Test
    void testCurrencyValues() {
        assertEquals("gbp", DiscogsCurrencies.GBP.getCurrency());
        assertEquals("usd", DiscogsCurrencies.USD.getCurrency());
    }
}
