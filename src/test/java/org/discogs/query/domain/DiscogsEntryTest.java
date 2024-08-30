package org.discogs.query.domain;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DiscogsEntryTest {

    @Test
    void testDtoCreation() {
        // Arrange
        DiscogsEntry dto = DiscogsEntry.builder()
                .id(1)
                .title("Sample Title")
                .format(Arrays.asList("vinyl", "CD"))
                .url("http://example.com/master")
                .uri("http://example.com/entry")
                .build();

        // Act & Assert
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Sample Title", dto.getTitle());
        assertEquals(Arrays.asList("vinyl", "CD"), dto.getFormat());
        assertEquals("http://example.com/master", dto.getUrl());
        assertEquals("http://example.com/entry", dto.getUri());
    }

    @Test
    void testDtoToString() {
        // Arrange
        DiscogsEntry dto = DiscogsEntry.builder()
                .id(1)
                .title("Sample Title")
                .format(Collections.singletonList("vinyl"))
                .url("http://example.com/master")
                .uri("http://example.com/entry")
                .build();

        // Act
        String dtoString = dto.toString();

        // Assert
        assertNotNull(dtoString);
        assertEquals("DiscogsEntry(id=1, title=Sample Title, format=[vinyl], url=http://example.com/master, uri=http://example.com/entry)", dtoString);
    }

}