package org.discogs.query.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing different currencies used in the Discogs Marketplace.
 * <p>
 * This enum provides predefined constants for various currencies, such as
 * GBP (British Pound) and USD (US Dollar).
 * Each constant is associated with a currency code string that represents
 * its name in the Discogs API.
 */
@Getter
@AllArgsConstructor
public enum DiscogsCurrencies {

    /**
     * Represents the British Pound (GBP) currency.
     */
    GBP("gbp"),

    /**
     * Represents the US Dollar (USD) currency.
     */
    USD("usd");

    /**
     * The currency code string associated with the enum constant.
     */
    private final String currency;
}