package org.discogs.query.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscogsVariousTest {

    @Test
    void testVariousValues() {
        assertEquals("various", DiscogsVarious.VARIOUS.getVariousName());
        assertEquals("various artists",
                DiscogsVarious.VARIOUS_ARTIST.getVariousName());
    }
}
