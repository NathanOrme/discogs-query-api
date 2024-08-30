package org.discogs.query.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscogFormatsTest {

    @Test
    void testEnumValues() {
        // Arrange
        DiscogFormats vinyl = DiscogFormats.VINYL;
        DiscogFormats comp = DiscogFormats.COMP;
        DiscogFormats vinylCompilation = DiscogFormats.VINYL_COMPILATION;

        // Act & Assert
        assertEquals("vinyl", vinyl.getFormat(), "The format for VINYL should be 'vinyl'");
        assertEquals("compilation", comp.getFormat(), "The format for COMP should be 'compilation'");
        assertEquals("compilation vinyl", vinylCompilation.getFormat(), "The format for VINYL_COMPILATION should be 'compilation vinyl'");
    }
}