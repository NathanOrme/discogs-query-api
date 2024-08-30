package org.discogs.query.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DiscogsEntryDTOTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    @Test
    void testJsonSerialization() throws JsonProcessingException {
        // Arrange
        DiscogsEntryDTO dto = DiscogsEntryDTO.builder()
                .id(1)
                .title("Sample Title")
                .format(Collections.singletonList("vinyl"))
                .url("http://example.com/master")
                .uri("http://example.com/entry")
                .build();

        // Act
        String json = objectMapper.writeValueAsString(dto);

        // Assert
        String expectedJson = "{\"id\":1,\"title\":\"Sample Title\",\"format\":[\"vinyl\"],\"master_url\":\"http://example.com/master\",\"uri\":\"http://example.com/entry\"}";
        assertEquals(expectedJson, json);
    }

    @Test
    void testJsonDeserialization() throws JsonProcessingException {
        // Arrange
        String json = "{\"id\":1,\"title\":\"Sample Title\",\"format\":[\"vinyl\"],\"master_url\":\"http://example.com/master\",\"uri\":\"http://example.com/entry\"}";

        // Act
        DiscogsEntryDTO dto = objectMapper.readValue(json, DiscogsEntryDTO.class);

        // Assert
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Sample Title", dto.getTitle());
        assertEquals(Collections.singletonList("vinyl"), dto.getFormat());
        assertEquals("http://example.com/master", dto.getUrl());
        assertEquals("http://example.com/entry", dto.getUri());
    }
}