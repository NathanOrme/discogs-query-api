package org.discogs.query.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DiscogsEntryDTOTest {


    @Test
    void testDtoCreation() {
        // Arrange
        DiscogsEntryDTO dto = DiscogsEntryDTO.builder()
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
        DiscogsEntryDTO dto = DiscogsEntryDTO.builder()
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
        assertEquals("DiscogsEntryDTO(id=1, title=Sample Title, format=[vinyl], url=http://example.com/master, uri=http://example.com/entry)", dtoString);
    }

}