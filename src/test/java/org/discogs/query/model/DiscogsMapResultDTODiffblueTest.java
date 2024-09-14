package org.discogs.query.model;

import org.discogs.query.model.enums.DiscogCountries;
import org.discogs.query.model.enums.DiscogsTypes;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiscogsMapResultDTODiffblueTest {
    /**
     * Methods under test:
     * <ul>
     *   <li>{@link DiscogsMapResultDTO#DiscogsMapResultDTO()}
     *   <li>{@link DiscogsMapResultDTO#setResults(Map)}
     *   <li>{@link DiscogsMapResultDTO#setSearchQuery(DiscogsQueryDTO)}
     *   <li>{@link DiscogsMapResultDTO#toString()}
     *   <li>{@link DiscogsMapResultDTO#getResults()}
     *   <li>{@link DiscogsMapResultDTO#getSearchQuery()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange and Act
        DiscogsMapResultDTO actualDiscogsMapResultDTO = new DiscogsMapResultDTO();
        HashMap<String, List<DiscogsEntryDTO>> results = new HashMap<>();
        actualDiscogsMapResultDTO.setResults(results);
        DiscogsQueryDTO searchQuery = DiscogsQueryDTO.builder()
                .album("Album")
                .artist("Artist")
                .barcode("Barcode")
                .country(DiscogCountries.UK)
                .format("Format")
                .track("Track")
                .types(DiscogsTypes.RELEASE)
                .build();
        actualDiscogsMapResultDTO.setSearchQuery(searchQuery);
        String actualToStringResult = actualDiscogsMapResultDTO.toString();
        Map<String, List<DiscogsEntryDTO>> actualResults = actualDiscogsMapResultDTO.getResults();
        DiscogsQueryDTO actualSearchQuery = actualDiscogsMapResultDTO.getSearchQuery();

        // Assert that nothing has changed
        assertEquals(
                "DiscogsMapResultDTO(searchQuery=DiscogsQueryDTO(artist=Artist, album=Album, track=Track, " + "format" +
                        "=Format,"
                        + " country=DiscogCountries.UK(countryName=UK), types=RELEASE, barcode=Barcode), results={})",
                actualToStringResult);
        assertTrue(actualResults.isEmpty());
        assertSame(results, actualResults);
        assertSame(searchQuery, actualSearchQuery);
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link DiscogsMapResultDTO#DiscogsMapResultDTO(DiscogsQueryDTO, Map)}
     *   <li>{@link DiscogsMapResultDTO#setResults(Map)}
     *   <li>{@link DiscogsMapResultDTO#setSearchQuery(DiscogsQueryDTO)}
     *   <li>{@link DiscogsMapResultDTO#toString()}
     *   <li>{@link DiscogsMapResultDTO#getResults()}
     *   <li>{@link DiscogsMapResultDTO#getSearchQuery()}
     * </ul>
     */
    @Test
    void testGettersAndSetters2() {
        // Arrange
        DiscogsQueryDTO searchQuery = DiscogsQueryDTO.builder()
                .album("Album")
                .artist("Artist")
                .barcode("Barcode")
                .country(DiscogCountries.UK)
                .format("Format")
                .track("Track")
                .types(DiscogsTypes.RELEASE)
                .build();

        // Act
        DiscogsMapResultDTO actualDiscogsMapResultDTO = new DiscogsMapResultDTO(searchQuery, new HashMap<>());
        HashMap<String, List<DiscogsEntryDTO>> results = new HashMap<>();
        actualDiscogsMapResultDTO.setResults(results);
        DiscogsQueryDTO searchQuery2 = DiscogsQueryDTO.builder()
                .album("Album")
                .artist("Artist")
                .barcode("Barcode")
                .country(DiscogCountries.UK)
                .format("Format")
                .track("Track")
                .types(DiscogsTypes.RELEASE)
                .build();
        actualDiscogsMapResultDTO.setSearchQuery(searchQuery2);
        String actualToStringResult = actualDiscogsMapResultDTO.toString();
        Map<String, List<DiscogsEntryDTO>> actualResults = actualDiscogsMapResultDTO.getResults();
        DiscogsQueryDTO actualSearchQuery = actualDiscogsMapResultDTO.getSearchQuery();

        // Assert that nothing has changed
        assertEquals(
                "DiscogsMapResultDTO(searchQuery=DiscogsQueryDTO(artist=Artist, album=Album, track=Track, " +
                        "format=Format,"
                        + " country=DiscogCountries.UK(countryName=UK), types=RELEASE, barcode=Barcode), results={})",
                actualToStringResult);
        assertTrue(actualResults.isEmpty());
        assertSame(results, actualResults);
        assertSame(searchQuery2, actualSearchQuery);
    }
}
