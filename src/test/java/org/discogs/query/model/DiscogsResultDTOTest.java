package org.discogs.query.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DiscogsResultDTOTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testDtoCreation() {
        // Arrange
        DiscogsEntryDTO entry = DiscogsEntryDTO.builder()
                .id(1)
                .title("Sample Title")
                .format(Collections.singletonList("vinyl"))
                .url("http://example.com/master")
                .uri("http://example.com/entry")
                .build();

        DiscogsResultDTO resultDTO = DiscogsResultDTO.builder()
                .results(Collections.singletonList(entry))
                .build();

        // Act & Assert
        assertNotNull(resultDTO);
        assertNotNull(resultDTO.getResults());
        assertEquals(1, resultDTO.getResults().size());
        assertEquals(entry, resultDTO.getResults().get(0));
    }

    @Test
    void testDtoToString() {
        // Arrange
        DiscogsEntryDTO entry = DiscogsEntryDTO.builder()
                .id(1)
                .title("Sample Title")
                .format(Collections.singletonList("vinyl"))
                .url("http://example.com/master")
                .uri("http://example.com/entry")
                .build();

        DiscogsResultDTO resultDTO = DiscogsResultDTO.builder()
                .results(Collections.singletonList(entry))
                .build();

        // Act
        String dtoString = resultDTO.toString();

        // Assert
        assertNotNull(dtoString);
        assertEquals("DiscogsResultDTO(results=[DiscogsEntryDTO(id=1, title=Sample Title, format=[vinyl], url=http://example.com/master, uri=http://example.com/entry)])", dtoString);
    }

}