package org.discogs.query.model.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test class for the {@link DiscogCountries} enum.
 *
 * <p>This class tests the functionality of the {@link DiscogCountries#fromString(String)} method,
 * ensuring that it correctly returns the expected enum constants based on input strings.
 */
class DiscogCountriesTest {

    /**
     * Test to ensure that the correct {@link DiscogCountries} constant is returned for each valid
     * country string.
     */
    @Test
    void fromString_WithValidCountryString_ShouldReturnCorrectEnum() {
        assertEquals(DiscogCountries.UK, DiscogCountries.fromString("UK"));
        assertEquals(DiscogCountries.US, DiscogCountries.fromString("US"));
        assertEquals(DiscogCountries.EUROPE, DiscogCountries.fromString("Europe"));
        assertEquals(DiscogCountries.COSTA_RICA, DiscogCountries.fromString("Costa Rica"));
        assertEquals(DiscogCountries.GERMANY, DiscogCountries.fromString("Germany"));
        assertEquals(DiscogCountries.ARGENTINA, DiscogCountries.fromString("Argentina"));
        assertEquals(DiscogCountries.THE_VATICAN, DiscogCountries.fromString("The Vatican"));
    }

    /**
     * Test to ensure that {@link DiscogCountries#UNKNOWN} is returned when an unknown or empty
     * country string is provided.
     */
    @Test
    void fromString_WithInvalidCountryString_ShouldReturnUnknown() {
        assertEquals(DiscogCountries.UNKNOWN, DiscogCountries.fromString("Atlantis"));
        assertEquals(DiscogCountries.UNKNOWN, DiscogCountries.fromString(""));
        assertEquals(DiscogCountries.UNKNOWN, DiscogCountries.fromString(null));
    }

    /**
     * Test to ensure that the {@link DiscogCountries#fromString(String)} method is case-insensitive.
     */
    @Test
    void fromString_WithCaseInsensitiveCountryString_ShouldReturnCorrectEnum() {
        assertEquals(DiscogCountries.UK, DiscogCountries.fromString("uk"));
        assertEquals(DiscogCountries.US, DiscogCountries.fromString("us"));
        assertEquals(DiscogCountries.COSTA_RICA, DiscogCountries.fromString("costa rica"));
        assertEquals(DiscogCountries.GERMANY, DiscogCountries.fromString("germany"));
    }
}
