package org.discogs.query.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscogsFormatsTest {

    @Test
    void testEnumValues() {
        // Arrange
        DiscogsFormats vinyl = DiscogsFormats.VINYL;
        DiscogsFormats comp = DiscogsFormats.COMP;
        DiscogsFormats vinylCompilation = DiscogsFormats.VINYL_COMPILATION;

        // Act & Assert
        assertEquals("vinyl", vinyl.getFormat(), "The format for VINYL should" +
                " be 'vinyl'");
        assertEquals("compilation", comp.getFormat(), "The format for COMP " +
                "should be 'compilation'");
        assertEquals("compilation vinyl", vinylCompilation.getFormat(), "The " +
                "format for VINYL_COMPILATION should be 'compilation vinyl'");
    }
}