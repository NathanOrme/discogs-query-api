package org.discogs.query.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscogsTypesTest {

    @Test
    void testEnumValues() {
        // Arrange
        DiscogsTypes release = DiscogsTypes.RELEASE;
        DiscogsTypes master = DiscogsTypes.MASTER;
        DiscogsTypes artist = DiscogsTypes.ARTIST;
        DiscogsTypes label = DiscogsTypes.LABEL;

        // Act & Assert
        assertEquals("release", release.getType(), "The type for RELEASE " +
                "should be 'release'");
        assertEquals("master", master.getType(), "The type for MASTER should " +
                "be 'master'");
        assertEquals("artist", artist.getType(), "The type for ARTIST should " +
                "be 'artist'");
        assertEquals("label", label.getType(), "The type for LABEL should be " +
                "'label'");
    }
}