package org.discogs.query.model.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test class for the {@link DiscogsTypes} enum.
 *
 * <p>This class tests the functionality of the {@link DiscogsTypes#fromString(String)} method,
 * ensuring that it correctly returns the expected enum constants based on input strings.
 */
class DiscogsTypesTest {

    /**
     * Test to ensure that the correct {@link DiscogsTypes} constant is returned for each valid type
     * string.
     */
    @Test
    void fromString_WithValidTypeString_ShouldReturnCorrectEnum() {
        assertEquals(DiscogsTypes.RELEASE, DiscogsTypes.fromString("release"));
        assertEquals(DiscogsTypes.MASTER, DiscogsTypes.fromString("master"));
        assertEquals(DiscogsTypes.ARTIST, DiscogsTypes.fromString("artist"));
        assertEquals(DiscogsTypes.LABEL, DiscogsTypes.fromString("label"));
    }

    /**
     * Test to ensure that {@link DiscogsTypes#UNKNOWN} is returned when an unknown or empty type
     * string is provided.
     */
    @Test
    void fromString_WithInvalidTypeString_ShouldReturnUnknown() {
        assertEquals(DiscogsTypes.UNKNOWN, DiscogsTypes.fromString("unknownType"));
        assertEquals(DiscogsTypes.UNKNOWN, DiscogsTypes.fromString(""));
        assertEquals(DiscogsTypes.UNKNOWN, DiscogsTypes.fromString(null));
    }

    /**
     * Test to ensure that the {@link DiscogsTypes#fromString(String)} method is case-insensitive.
     */
    @Test
    void fromString_WithCaseInsensitiveTypeString_ShouldReturnCorrectEnum() {
        assertEquals(DiscogsTypes.RELEASE, DiscogsTypes.fromString("ReLeAsE"));
        assertEquals(DiscogsTypes.MASTER, DiscogsTypes.fromString("MaStEr"));
        assertEquals(DiscogsTypes.ARTIST, DiscogsTypes.fromString("ArTiSt"));
        assertEquals(DiscogsTypes.LABEL, DiscogsTypes.fromString("LaBeL"));
    }
}
