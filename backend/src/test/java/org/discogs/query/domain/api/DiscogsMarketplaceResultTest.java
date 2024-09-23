package org.discogs.query.domain.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DiscogsMarketplaceResultTest {

    @Test
    void testDefaultConstructor() {
        DiscogsMarketplaceResult result = new DiscogsMarketplaceResult();
        assertNull(result.getResult());
        assertNull(result.getNumberForSale());
    }

    @Test
    void testAllArgsConstructor() {
        DiscogsLowestPriceResult lowestPrice = new DiscogsLowestPriceResult("USD", 9.99f);
        DiscogsMarketplaceResult result = new DiscogsMarketplaceResult(lowestPrice, 15);

        assertEquals(lowestPrice, result.getResult());
        assertEquals(15, result.getNumberForSale());
    }

    @Test
    void testBuilder() {
        DiscogsLowestPriceResult lowestPrice = DiscogsLowestPriceResult.builder()
                .currency("GBP")
                .value(12.50f)
                .build();

        DiscogsMarketplaceResult result = DiscogsMarketplaceResult.builder()
                .result(lowestPrice)
                .numberForSale(20)
                .build();

        assertEquals(lowestPrice, result.getResult());
        assertEquals(20, result.getNumberForSale());
    }

    @Test
    void testSetterAndGetter() {
        DiscogsMarketplaceResult result = new DiscogsMarketplaceResult();

        DiscogsLowestPriceResult lowestPrice = new DiscogsLowestPriceResult("USD", 15.00f);
        result.setResult(lowestPrice);
        result.setNumberForSale(30);

        assertEquals(lowestPrice, result.getResult());
        assertEquals(30, result.getNumberForSale());
    }

}

