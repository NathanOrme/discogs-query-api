package org.discogs.query.annotations;

import org.discogs.query.model.DiscogsQueryDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VariousArtistsValidatorTest {

    private final VariousArtistsValidator validator = new VariousArtistsValidator();

    @Test
    void testValidCase() {
        DiscogsQueryDTO dto = new DiscogsQueryDTO("Various Artists", "Some Album", "Some Track", null, null, null);
        boolean result = validator.isValid(dto, null);
        assertTrue(result);
    }

    @Test
    void testInvalidCase() {
        DiscogsQueryDTO dto = new DiscogsQueryDTO("Various Artists", null, null, null, null, null);
        boolean result = validator.isValid(dto, null);
        assertFalse(result);
    }
}

