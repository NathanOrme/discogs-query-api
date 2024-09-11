package org.discogs.query.mapper;

import org.discogs.query.domain.DiscogsEntry;
import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.enums.DiscogCountries;
import org.discogs.query.enums.DiscogsTypes;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {DiscogsResultMapper.class})
@ExtendWith(SpringExtension.class)
class DiscogsResultMapperDiffblueTest {
    @Autowired
    private DiscogsResultMapper discogsResultMapper;

    /**
     * Method under test:
     * {@link DiscogsResultMapper#mapObjectToDTO(DiscogsResult, DiscogsQueryDTO)}
     */
    @Test
    void testMapObjectToDTO() {
        // Arrange
        DiscogsResult discogsResult = new DiscogsResult();
        DiscogsQueryDTO discogsQueryDTO = new DiscogsQueryDTO("Artist", "Album", "Track", "Format", DiscogCountries.UK,
                DiscogsTypes.RELEASE, "Barcode");

        // Act
        DiscogsResultDTO actualMapObjectToDTOResult = discogsResultMapper.mapObjectToDTO(discogsResult,
                discogsQueryDTO);

        // Assert
        assertNull(actualMapObjectToDTOResult.getResults());
        assertSame(discogsQueryDTO, actualMapObjectToDTOResult.getSearchQuery());
    }

    /**
     * Method under test:
     * {@link DiscogsResultMapper#mapObjectToDTO(DiscogsResult, DiscogsQueryDTO)}
     */
    @Test
    void testMapObjectToDTO2() {
        // Arrange
        DiscogsResult.DiscogsResultBuilder builderResult = DiscogsResult.builder();
        DiscogsResult discogsResult = builderResult.results(new ArrayList<>()).build();
        DiscogsQueryDTO discogsQueryDTO = DiscogsQueryDTO.builder()
                .album("Album")
                .artist("Artist")
                .barcode("Barcode")
                .country(DiscogCountries.UK)
                .format("Format")
                .track("Track")
                .types(DiscogsTypes.RELEASE)
                .build();

        // Act
        DiscogsResultDTO actualMapObjectToDTOResult = discogsResultMapper.mapObjectToDTO(discogsResult,
                discogsQueryDTO);

        // Assert
        assertTrue(actualMapObjectToDTOResult.getResults().isEmpty());
        assertSame(discogsQueryDTO, actualMapObjectToDTOResult.getSearchQuery());
    }

    /**
     * Method under test:
     * {@link DiscogsResultMapper#mapObjectToDTO(DiscogsResult, DiscogsQueryDTO)}
     */
    @Test
    void testMapObjectToDTO3() {
        // Arrange
        DiscogsResult discogsResult = mock(DiscogsResult.class);
        when(discogsResult.getResults()).thenReturn(new ArrayList<>());
        DiscogsQueryDTO discogsQueryDTO = new DiscogsQueryDTO("Artist", "Album", "Track", "Format", DiscogCountries.UK,
                DiscogsTypes.RELEASE, "Barcode");

        // Act
        DiscogsResultDTO actualMapObjectToDTOResult = discogsResultMapper.mapObjectToDTO(discogsResult,
                discogsQueryDTO);

        // Assert
        verify(discogsResult).getResults();
        assertTrue(actualMapObjectToDTOResult.getResults().isEmpty());
        assertSame(discogsQueryDTO, actualMapObjectToDTOResult.getSearchQuery());
    }

    /**
     * Method under test:
     * {@link DiscogsResultMapper#mapObjectToDTO(DiscogsResult, DiscogsQueryDTO)}
     */
    @Test
    void testMapObjectToDTO4() {
        // Arrange
        ArrayList<DiscogsEntry> discogsEntryList = new ArrayList<>();
        DiscogsEntry.DiscogsEntryBuilder countryResult = DiscogsEntry.builder().country("GB");
        DiscogsEntry buildResult = countryResult.format(new ArrayList<>())
                .id(1)
                .lowestPrice(10.0f)
                .numberForSale(10)
                .title("Dr")
                .uri("Uri")
                .url("https://example.org/example")
                .year("Year")
                .build();
        discogsEntryList.add(buildResult);
        DiscogsResult discogsResult = mock(DiscogsResult.class);
        when(discogsResult.getResults()).thenReturn(discogsEntryList);
        DiscogsQueryDTO discogsQueryDTO = new DiscogsQueryDTO("Artist", "Album", "Track", "Format", DiscogCountries.UK,
                DiscogsTypes.RELEASE, "Barcode");

        // Act
        DiscogsResultDTO actualMapObjectToDTOResult = discogsResultMapper.mapObjectToDTO(discogsResult,
                discogsQueryDTO);

        // Assert
        verify(discogsResult).getResults();
        List<DiscogsEntryDTO> results = actualMapObjectToDTOResult.getResults();
        assertEquals(1, results.size());
        DiscogsEntryDTO getResult = results.get(0);
        assertEquals("Dr", getResult.getTitle());
        assertEquals("GB", getResult.getCountry());
        assertEquals("Uri", getResult.getUri());
        assertEquals("Year", getResult.getYear());
        assertEquals("https://example.org/example", getResult.getUrl());
        assertNull(getResult.getIsOnMarketplace());
        assertEquals(1, getResult.getId());
        assertEquals(10, getResult.getNumberForSale().intValue());
        assertEquals(10.0f, getResult.getLowestPrice().floatValue());
        assertTrue(getResult.getFormat().isEmpty());
        assertSame(discogsQueryDTO, actualMapObjectToDTOResult.getSearchQuery());
    }

    /**
     * Method under test:
     * {@link DiscogsResultMapper#mapObjectToDTO(DiscogsResult, DiscogsQueryDTO)}
     */
    @Test
    void testMapObjectToDTO5() {
        // Arrange
        ArrayList<DiscogsEntry> discogsEntryList = new ArrayList<>();
        DiscogsEntry.DiscogsEntryBuilder countryResult = DiscogsEntry.builder().country("GB");
        DiscogsEntry buildResult = countryResult.format(new ArrayList<>())
                .id(1)
                .lowestPrice(10.0f)
                .numberForSale(10)
                .title("Dr")
                .uri("Uri")
                .url("https://example.org/example")
                .year("Year")
                .build();
        discogsEntryList.add(buildResult);
        DiscogsEntry.DiscogsEntryBuilder countryResult2 = DiscogsEntry.builder().country("GB");
        DiscogsEntry buildResult2 = countryResult2.format(new ArrayList<>())
                .id(1)
                .lowestPrice(10.0f)
                .numberForSale(10)
                .title("Dr")
                .uri("Uri")
                .url("https://example.org/example")
                .year("Year")
                .build();
        discogsEntryList.add(buildResult2);
        DiscogsResult discogsResult = mock(DiscogsResult.class);
        when(discogsResult.getResults()).thenReturn(discogsEntryList);
        DiscogsQueryDTO discogsQueryDTO = new DiscogsQueryDTO("Artist", "Album", "Track", "Format", DiscogCountries.UK,
                DiscogsTypes.RELEASE, "Barcode");

        // Act
        DiscogsResultDTO actualMapObjectToDTOResult = discogsResultMapper.mapObjectToDTO(discogsResult,
                discogsQueryDTO);

        // Assert
        verify(discogsResult).getResults();
        List<DiscogsEntryDTO> results = actualMapObjectToDTOResult.getResults();
        assertEquals(2, results.size());
        DiscogsEntryDTO getResult = results.get(0);
        assertEquals("Dr", getResult.getTitle());
        DiscogsEntryDTO getResult2 = results.get(1);
        assertEquals("Dr", getResult2.getTitle());
        assertEquals("GB", getResult.getCountry());
        assertEquals("GB", getResult2.getCountry());
        assertEquals("Uri", getResult.getUri());
        assertEquals("Uri", getResult2.getUri());
        assertEquals("Year", getResult.getYear());
        assertEquals("Year", getResult2.getYear());
        assertEquals("https://example.org/example", getResult.getUrl());
        assertEquals("https://example.org/example", getResult2.getUrl());
        assertNull(getResult.getIsOnMarketplace());
        assertNull(getResult2.getIsOnMarketplace());
        assertEquals(1, getResult.getId());
        assertEquals(1, getResult2.getId());
        assertEquals(10, getResult.getNumberForSale().intValue());
        assertEquals(10, getResult2.getNumberForSale().intValue());
        assertEquals(10.0f, getResult.getLowestPrice().floatValue());
        assertEquals(10.0f, getResult2.getLowestPrice().floatValue());
        assertTrue(getResult.getFormat().isEmpty());
        assertTrue(getResult2.getFormat().isEmpty());
        assertSame(discogsQueryDTO, actualMapObjectToDTOResult.getSearchQuery());
    }
}
