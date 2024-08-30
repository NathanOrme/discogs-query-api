package org.discogs.query.model;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    @Test
    void testJsonSerialization() throws JsonProcessingException {
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
        String json = objectMapper.writeValueAsString(resultDTO);

        // Assert
        String expectedJson = "{\"results\":[{\"id\":1,\"title\":\"Sample Title\",\"format\":[\"vinyl\"],\"master_url\":\"http://example.com/master\",\"uri\":\"http://example.com/entry\"}]}";
        assertEquals(expectedJson, json);
    }

    @Test
    void testJsonDeserialization() throws JsonProcessingException {
        // Arrange
        String json = "{\"results\":[{\"id\":1,\"title\":\"Sample Title\",\"format\":[\"vinyl\"],\"master_url\":\"http://example.com/master\",\"uri\":\"http://example.com/entry\"}]}";

        // Act
        DiscogsResultDTO resultDTO = objectMapper.readValue(json, DiscogsResultDTO.class);

        // Assert
        assertNotNull(resultDTO);
        assertNotNull(resultDTO.getResults());
        assertEquals(1, resultDTO.getResults().size());

        DiscogsEntryDTO entry = resultDTO.getResults().get(0);
        assertEquals(1, entry.getId());
        assertEquals("Sample Title", entry.getTitle());
        assertEquals(Collections.singletonList("vinyl"), entry.getFormat());
        assertEquals("http://example.com/master", entry.getUrl());
        assertEquals("http://example.com/entry", entry.getUri());
    }
}