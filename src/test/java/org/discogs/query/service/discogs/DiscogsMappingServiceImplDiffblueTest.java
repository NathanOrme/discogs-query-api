package org.discogs.query.service.discogs;

import org.discogs.query.enums.DiscogCountries;
import org.discogs.query.enums.DiscogsTypes;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {DiscogsMappingServiceImpl.class})
@ExtendWith(SpringExtension.class)
class DiscogsMappingServiceImplDiffblueTest {

    @Autowired
    private DiscogsMappingServiceImpl discogsMappingServiceImpl;

    /**
     * Method under test:
     * {@link DiscogsMappingServiceImpl#convertEntriesToMapByTitle(DiscogsResultDTO)}
     */
    @Test
    void testConvertEntriesToMapByTitle() {
        // Arrange
        DiscogsResultDTO.DiscogsResultDTOBuilder builderResult = DiscogsResultDTO.builder();
        DiscogsResultDTO discogsResultDTO = builderResult.results(new ArrayList<>()).build();

        // Act
        DiscogsMapResultDTO actualConvertListToMapForDTOResult = discogsMappingServiceImpl
                .convertEntriesToMapByTitle(discogsResultDTO);

        // Assert
        assertNull(actualConvertListToMapForDTOResult.getSearchQuery());
        assertTrue(actualConvertListToMapForDTOResult.getResults().isEmpty());
    }

    /**
     * Method under test:
     * {@link DiscogsMappingServiceImpl#convertEntriesToMapByTitle(DiscogsResultDTO)}
     */
    @Test
    void testConvertEntriesToMapByTitle2() {
        // Arrange
        DiscogsResultDTO discogsResultDTO = mock(DiscogsResultDTO.class);
        when(discogsResultDTO.getResults()).thenReturn(new ArrayList<>());
        DiscogsQueryDTO buildResult = DiscogsQueryDTO.builder()
                .album("Album")
                .artist("Artist")
                .barcode("Barcode")
                .country(DiscogCountries.UK)
                .format("Format")
                .track("Track")
                .types(DiscogsTypes.RELEASE)
                .build();
        when(discogsResultDTO.getSearchQuery()).thenReturn(buildResult);

        // Act
        DiscogsMapResultDTO actualConvertListToMapForDTOResult = discogsMappingServiceImpl
                .convertEntriesToMapByTitle(discogsResultDTO);

        // Assert
        verify(discogsResultDTO).getResults();
        verify(discogsResultDTO).getSearchQuery();
        DiscogsQueryDTO searchQuery = actualConvertListToMapForDTOResult.getSearchQuery();
        assertEquals("Album", searchQuery.getAlbum());
        assertEquals("Artist", searchQuery.getArtist());
        assertEquals("Barcode", searchQuery.getBarcode());
        assertEquals("Format", searchQuery.getFormat());
        assertEquals("Track", searchQuery.getTrack());
        assertEquals(DiscogCountries.UK, searchQuery.getCountry());
        assertEquals(DiscogsTypes.RELEASE, searchQuery.getTypes());
        assertTrue(actualConvertListToMapForDTOResult.getResults().isEmpty());
    }

    /**
     * Method under test:
     * {@link DiscogsMappingServiceImpl#convertEntriesToMapByTitle(DiscogsResultDTO)}
     */
    @Test
    void testConvertEntriesToMapByTitle3() {
        // Arrange
        ArrayList<DiscogsEntryDTO> discogsEntryDTOList = new ArrayList<>();
        DiscogsEntryDTO.DiscogsEntryDTOBuilder countryResult = DiscogsEntryDTO.builder().country("GB");
        DiscogsEntryDTO buildResult = countryResult.format(new ArrayList<>())
                .id(1)
                .lowestPrice(10.0f)
                .numberForSale(10)
                .title("Dr")
                .uri("Uri")
                .url("https://example.org/example")
                .year("Year")
                .build();
        discogsEntryDTOList.add(buildResult);
        DiscogsResultDTO discogsResultDTO = mock(DiscogsResultDTO.class);
        when(discogsResultDTO.getResults()).thenReturn(discogsEntryDTOList);
        DiscogsQueryDTO buildResult2 = DiscogsQueryDTO.builder()
                .album("Album")
                .artist("Artist")
                .barcode("Barcode")
                .country(DiscogCountries.UK)
                .format("Format")
                .track("Track")
                .types(DiscogsTypes.RELEASE)
                .build();
        when(discogsResultDTO.getSearchQuery()).thenReturn(buildResult2);

        // Act
        DiscogsMapResultDTO actualConvertListToMapForDTOResult = discogsMappingServiceImpl
                .convertEntriesToMapByTitle(discogsResultDTO);

        // Assert
        verify(discogsResultDTO).getResults();
        verify(discogsResultDTO).getSearchQuery();
        DiscogsQueryDTO searchQuery = actualConvertListToMapForDTOResult.getSearchQuery();
        assertEquals("Album", searchQuery.getAlbum());
        assertEquals("Artist", searchQuery.getArtist());
        assertEquals("Barcode", searchQuery.getBarcode());
        assertEquals("Format", searchQuery.getFormat());
        assertEquals("Track", searchQuery.getTrack());
        Map<String, List<DiscogsEntryDTO>> results = actualConvertListToMapForDTOResult.getResults();
        assertEquals(1, results.size());
        assertEquals(DiscogCountries.UK, searchQuery.getCountry());
        assertEquals(DiscogsTypes.RELEASE, searchQuery.getTypes());
        assertEquals(discogsEntryDTOList, results.get("Dr"));
    }

    /**
     * Method under test:
     * {@link DiscogsMappingServiceImpl#convertEntriesToMapByTitle(DiscogsResultDTO)}
     */
    @Test
    void testConvertEntriesToMapByTitle4() {
        // Arrange
        ArrayList<DiscogsEntryDTO> discogsEntryDTOList = new ArrayList<>();
        DiscogsEntryDTO.DiscogsEntryDTOBuilder countryResult = DiscogsEntryDTO.builder().country("GB");
        DiscogsEntryDTO buildResult = countryResult.format(new ArrayList<>())
                .id(1)
                .lowestPrice(10.0f)
                .numberForSale(10)
                .title("Dr")
                .uri("Uri")
                .url("https://example.org/example")
                .year("Year")
                .build();
        discogsEntryDTOList.add(buildResult);
        DiscogsEntryDTO.DiscogsEntryDTOBuilder countryResult2 = DiscogsEntryDTO.builder().country("GB");
        DiscogsEntryDTO buildResult2 = countryResult2.format(new ArrayList<>())
                .id(1)
                .lowestPrice(10.0f)
                .numberForSale(10)
                .title("Dr")
                .uri("Uri")
                .url("https://example.org/example")
                .year("Year")
                .build();
        discogsEntryDTOList.add(buildResult2);
        DiscogsResultDTO discogsResultDTO = mock(DiscogsResultDTO.class);
        when(discogsResultDTO.getResults()).thenReturn(discogsEntryDTOList);
        DiscogsQueryDTO buildResult3 = DiscogsQueryDTO.builder()
                .album("Album")
                .artist("Artist")
                .barcode("Barcode")
                .country(DiscogCountries.UK)
                .format("Format")
                .track("Track")
                .types(DiscogsTypes.RELEASE)
                .build();
        when(discogsResultDTO.getSearchQuery()).thenReturn(buildResult3);

        // Act
        DiscogsMapResultDTO actualConvertListToMapForDTOResult = discogsMappingServiceImpl
                .convertEntriesToMapByTitle(discogsResultDTO);

        // Assert
        verify(discogsResultDTO).getResults();
        verify(discogsResultDTO).getSearchQuery();
        DiscogsQueryDTO searchQuery = actualConvertListToMapForDTOResult.getSearchQuery();
        assertEquals("Album", searchQuery.getAlbum());
        assertEquals("Artist", searchQuery.getArtist());
        assertEquals("Barcode", searchQuery.getBarcode());
        assertEquals("Format", searchQuery.getFormat());
        assertEquals("Track", searchQuery.getTrack());
        Map<String, List<DiscogsEntryDTO>> results = actualConvertListToMapForDTOResult.getResults();
        assertEquals(1, results.size());
        assertEquals(DiscogCountries.UK, searchQuery.getCountry());
        assertEquals(DiscogsTypes.RELEASE, searchQuery.getTypes());
        assertEquals(discogsEntryDTOList, results.get("Dr"));
    }
}
