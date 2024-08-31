package org.discogs.query.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents the lowest price result for an item in the Discogs Marketplace.
 * <p>
 * This class contains information about the lowest price of an item, including the currency and the price value.
 * </p>
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DiscogsLowestPriceResult {

    /**
     * The currency of the lowest price.
     * <p>
     * This field holds the currency code (e.g., "USD", "GBP") for the price.
     * </p>
     */
    private String currency;

    /**
     * The value of the lowest price.
     * <p>
     * This field holds the numerical value of the lowest price in the specified currency.
     * </p>
     */
    private float value;
}