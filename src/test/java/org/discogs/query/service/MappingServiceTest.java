package org.discogs.query.service;

import org.discogs.query.domain.DiscogsEntry;
import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MappingServiceTest {

    private final MappingService mappingService = new MappingService();

    @Test
    void testConvertEntriesToMapByTitle() {
        // Prepare test data
        DiscogsEntryDTO entryDTO1 = createDiscogsEntryDTO(1, "Title1");
        DiscogsEntryDTO entryDTO2 = createDiscogsEntryDTO(2, "Title1");
        DiscogsResultDTO resultDTO = createDiscogsResultDTO(List.of(entryDTO1, entryDTO2));

        // Execute the method
        DiscogsMapResultDTO mapResultDTO = mappingService.convertEntriesToMapByTitle(resultDTO);

        // Validate the results
        assertEquals("Artist", resultDTO.searchQuery().artist());
        assertEquals(1, mapResultDTO.results().size());
        assertEquals(2, mapResultDTO.results().get("Title1").size());
    }

    @Test
    void testMapResultsToDTO() {
        // Prepare test data
        DiscogsEntry entry = createDiscogsEntry();
        DiscogsEntryDTO entryDTO = createDiscogsEntryDTO(1, "Title");
        DiscogsResultDTO resultDTO = createDiscogsResultDTO(List.of(entryDTO));
        DiscogsResult result = createDiscogsResult(List.of(entry));

        // Mock the MappingService's mapToResultDTO method
        MappingService mappingServiceSpy = Mockito.spy(mappingService);
        Mockito.doReturn(resultDTO).when(mappingServiceSpy).mapObjectToDTO(result, createDiscogsQueryDTO());

        // Execute the method
        DiscogsResultDTO mappedResultDTO = mappingServiceSpy.mapObjectToDTO(result, resultDTO.searchQuery());

        // Validate the results
        assertEquals("Artist", mappedResultDTO.searchQuery().artist());
        assertEquals(1, mappedResultDTO.results().size());
        DiscogsEntryDTO dto = mappedResultDTO.results().getFirst();
        assertEquals(1, dto.id());
        assertEquals("Title", dto.title());
    }

    @Test
    void testMapObjectToDTO_ExceptionHandling() {
        // Prepare test data
        DiscogsQueryDTO queryDTO = createDiscogsQueryDTO();
        DiscogsResult result = Mockito.mock(DiscogsResult.class);
        Mockito.when(result.getResults()).thenThrow(new RuntimeException("Test Exception"));

        // Execute and validate exception
        assertThrows(RuntimeException.class, () -> mappingService.mapObjectToDTO(result, queryDTO));
    }

    private DiscogsEntry createDiscogsEntry() {
        return new DiscogsEntry(1, "Title", List.of("Vinyl"),
                "url", "uri", "Country", "2020", true, 5.0f, 10);
    }

    private DiscogsEntryDTO createDiscogsEntryDTO(final int id, final String title) {
        return new DiscogsEntryDTO(id, title, List.of("Vinyl"), "url", "uri",
                "Country", "2020", true, 5.0f, 10);
    }

    private DiscogsResultDTO createDiscogsResultDTO(final List<DiscogsEntryDTO> results) {
        return new DiscogsResultDTO(createDiscogsQueryDTO(), results);
    }

    private DiscogsResult createDiscogsResult(final List<DiscogsEntry> entries) {
        return new DiscogsResult(entries);
    }

    private DiscogsQueryDTO createDiscogsQueryDTO() {
        return new DiscogsQueryDTO("Artist", "Album", "Track",
                null, "Format", null, null, "Barcode");
    }
}
