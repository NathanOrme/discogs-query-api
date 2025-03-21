package org.discogs.query.model.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test class for the {@link DiscogsFormats} enum.
 *
 * <p>This class tests the functionality of the {@link DiscogsFormats#fromString(String)} method,
 * ensuring that it correctly returns the expected enum constants based on input strings.
 */
class DiscogsFormatsTest {

    /**
     * Test to ensure that the correct {@link DiscogsFormats} constant is returned for each valid
     * format string.
     */
    @Test
    void fromString_WithValidFormatString_ShouldReturnCorrectEnum() {
        assertEquals(DiscogsFormats.LP, DiscogsFormats.fromString("lp"));
        assertEquals(DiscogsFormats.ALBUM, DiscogsFormats.fromString("album"));
        assertEquals(DiscogsFormats.CD, DiscogsFormats.fromString("cd"));
        assertEquals(DiscogsFormats.VINYL, DiscogsFormats.fromString("vinyl"));
        assertEquals(DiscogsFormats.COMP, DiscogsFormats.fromString("compilation"));
        assertEquals(DiscogsFormats.VINYL_COMPILATION, DiscogsFormats.fromString("compilation vinyl"));
        assertEquals(DiscogsFormats.VINYL_ALBUM, DiscogsFormats.fromString("album vinyl"));
        assertEquals(DiscogsFormats.ALL_VINYLS, DiscogsFormats.fromString("all vinyls"));
    }

    /**
     * Test to ensure that {@link DiscogsFormats#UNKNOWN} is returned when an unknown or empty format
     * string is provided.
     */
    @Test
    void fromString_WithInvalidFormatString_ShouldReturnUnknown() {
        assertEquals(DiscogsFormats.UNKNOWN, DiscogsFormats.fromString("unknownFormat"));
        assertEquals(DiscogsFormats.UNKNOWN, DiscogsFormats.fromString(""));
        assertEquals(DiscogsFormats.UNKNOWN, DiscogsFormats.fromString(null));
    }

    /**
     * Test to ensure that the {@link DiscogsFormats#fromString(String)} method is case-insensitive.
     */
    @Test
    void fromString_WithCaseInsensitiveFormatString_ShouldReturnCorrectEnum() {
        assertEquals(DiscogsFormats.LP, DiscogsFormats.fromString("Lp"));
        assertEquals(DiscogsFormats.ALBUM, DiscogsFormats.fromString("ALBUM"));
        assertEquals(DiscogsFormats.CD, DiscogsFormats.fromString("Cd"));
        assertEquals(DiscogsFormats.VINYL, DiscogsFormats.fromString("VINYL"));
        assertEquals(DiscogsFormats.COMP, DiscogsFormats.fromString("Compilation"));
        assertEquals(DiscogsFormats.VINYL_COMPILATION, DiscogsFormats.fromString("COMPILATION VINYL"));
        assertEquals(DiscogsFormats.VINYL_ALBUM, DiscogsFormats.fromString("Album Vinyl"));
        assertEquals(DiscogsFormats.ALL_VINYLS, DiscogsFormats.fromString("All Vinyls"));
    }
}
