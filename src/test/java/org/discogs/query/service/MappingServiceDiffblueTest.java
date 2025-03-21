package org.discogs.query.service;

import org.discogs.query.domain.api.DiscogsEntry;
import org.discogs.query.domain.api.DiscogsResult;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.discogs.query.model.enums.DiscogCountries;
import org.discogs.query.model.enums.DiscogsTypes;
import org.junit.jupiter.api.DisplayName;
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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {MappingService.class})
@ExtendWith(SpringExtension.class)
class MappingServiceDiffblueTest {
    @Autowired
    private MappingService mappingService;

    private static ArrayList<DiscogsResultDTO> getDiscogsResultDTOS(
            final ArrayList<DiscogsEntryDTO> results) {
        DiscogsResultDTO discogsResultDTO =
                new DiscogsResultDTO(
                        new DiscogsQueryDTO(
                                "Artist",
                                "Album",
                                "Track",
                                "Dr",
                                "Format",
                                DiscogCountries.UK,
                                DiscogsTypes.RELEASE,
                                "Barcode"),
                        results);

        ArrayList<DiscogsResultDTO> resultDTOList = new ArrayList<>();
        resultDTOList.add(discogsResultDTO);
        DiscogsQueryDTO searchQuery =
                new DiscogsQueryDTO(
                        "Artist",
                        "Album",
                        "Track",
                        "Dr",
                        "Format",
                        DiscogCountries.UK,
                        DiscogsTypes.RELEASE,
                        "Barcode");

        resultDTOList.add(new DiscogsResultDTO(searchQuery, new ArrayList<>()));
        return resultDTOList;
    }

    /**
     * Test {@link MappingService#convertEntriesToMapByTitle(DiscogsResultDTO)}.
     *
     * <p>Method under test: {@link MappingService#convertEntriesToMapByTitle(DiscogsResultDTO)}
     */
    @Test
    @DisplayName("Test convertEntriesToMapByTitle(DiscogsResultDTO)")
    void testConvertEntriesToMapByTitle() {
        // Arrange
        DiscogsQueryDTO searchQuery =
                new DiscogsQueryDTO(
                        "Artist",
                        "Album",
                        "Track",
                        "Dr",
                        "Format",
                        DiscogCountries.UK,
                        DiscogsTypes.RELEASE,
                        "Barcode");

        // Act
        DiscogsMapResultDTO actualConvertEntriesToMapByTitleResult =
                mappingService.convertEntriesToMapByTitle(
                        new DiscogsResultDTO(searchQuery, new ArrayList<>()));

        // Assert
        assertNull(actualConvertEntriesToMapByTitleResult.cheapestItem());
        assertTrue(actualConvertEntriesToMapByTitleResult.results().isEmpty());
        assertSame(searchQuery, actualConvertEntriesToMapByTitleResult.searchQuery());
    }

    /**
     * Test {@link MappingService#convertEntriesToMapByTitle(DiscogsResultDTO)}.
     *
     * <p>Method under test: {@link MappingService#convertEntriesToMapByTitle(DiscogsResultDTO)}
     */
    @Test
    @DisplayName("Test convertEntriesToMapByTitle(DiscogsResultDTO)")
    void testConvertEntriesToMapByTitle2() {
        // Arrange
        DiscogsQueryDTO searchQuery =
                new DiscogsQueryDTO(
                        "DiscogsResultDTO must not be null",
                        "Album",
                        "Track",
                        "Dr",
                        "Format",
                        DiscogCountries.UK,
                        DiscogsTypes.RELEASE,
                        "Barcode");

        // Act
        DiscogsMapResultDTO actualConvertEntriesToMapByTitleResult =
                mappingService.convertEntriesToMapByTitle(
                        new DiscogsResultDTO(searchQuery, new ArrayList<>()));

        // Assert
        assertNull(actualConvertEntriesToMapByTitleResult.cheapestItem());
        assertTrue(actualConvertEntriesToMapByTitleResult.results().isEmpty());
        assertSame(searchQuery, actualConvertEntriesToMapByTitleResult.searchQuery());
    }

    /**
     * Test {@link MappingService#mapResultsToDTO(List)}.
     *
     * <ul>
     *   <li>Then return first results size is one.
     * </ul>
     *
     * <p>Method under test: {@link MappingService#mapResultsToDTO(List)}
     */
    @Test
    @DisplayName("Test mapResultsToDTO(List); then return first results size is one")
    void testMapResultsToDTO_thenReturnFirstResultsSizeIsOne() {
        // Arrange
        ArrayList<DiscogsEntryDTO> results = new ArrayList<>();
        DiscogsEntryDTO discogsEntryDTO =
                new DiscogsEntryDTO(
                        1,
                        "Dr",
                        new ArrayList<>(),
                        "https://example.org/example",
                        "DiscogsResultDTO must not be null",
                        "GB",
                        "",
                        true,
                        10.0f,
                        10);

        results.add(discogsEntryDTO);
        var resultDTOList = getDiscogsResultDTOS(results);

        // Act
        List<DiscogsMapResultDTO> actualMapResultsToDTOResult =
                mappingService.mapResultsToDTO(resultDTOList);

        // Assert
        assertEquals(2, actualMapResultsToDTOResult.size());
        DiscogsMapResultDTO getResult = actualMapResultsToDTOResult.getFirst();
        Map<String, List<DiscogsEntryDTO>> resultsResult = getResult.results();
        assertEquals(1, resultsResult.size());
        assertEquals(results, resultsResult.get("Dr"));
        assertSame(discogsEntryDTO, getResult.cheapestItem());
    }

    /**
     * Test {@link MappingService#mapResultsToDTO(List)}.
     *
     * <ul>
     *   <li>Then return first searchQuery title is {@code 42}.
     * </ul>
     *
     * <p>Method under test: {@link MappingService#mapResultsToDTO(List)}
     */
    @Test
    @DisplayName("Test mapResultsToDTO(List); then return first searchQuery title is '42'")
    void testMapResultsToDTO_thenReturnFirstSearchQueryTitleIs42() {
        // Arrange
        ArrayList<DiscogsResultDTO> resultDTOList = new ArrayList<>();
        DiscogsQueryDTO searchQuery =
                new DiscogsQueryDTO(
                        "Artist",
                        "Album",
                        "Track",
                        "42",
                        "Format",
                        DiscogCountries.UK,
                        DiscogsTypes.RELEASE,
                        "Barcode");

        resultDTOList.add(new DiscogsResultDTO(searchQuery, new ArrayList<>()));
        DiscogsQueryDTO searchQuery2 =
                new DiscogsQueryDTO(
                        "Artist",
                        "Album",
                        "Track",
                        "Dr",
                        "Format",
                        DiscogCountries.UK,
                        DiscogsTypes.RELEASE,
                        "Barcode");

        resultDTOList.add(new DiscogsResultDTO(searchQuery2, new ArrayList<>()));

        // Act
        List<DiscogsMapResultDTO> actualMapResultsToDTOResult =
                mappingService.mapResultsToDTO(resultDTOList);

        // Assert
        assertEquals(2, actualMapResultsToDTOResult.size());
        DiscogsQueryDTO searchQueryResult = actualMapResultsToDTOResult.get(0).searchQuery();
        assertEquals("42", searchQueryResult.title());
        DiscogsQueryDTO searchQueryResult2 = actualMapResultsToDTOResult.get(1).searchQuery();
        assertEquals("Album", searchQueryResult2.album());
        assertEquals("Artist", searchQueryResult2.artist());
        assertEquals("Barcode", searchQueryResult2.barcode());
        assertEquals("Dr", searchQueryResult2.title());
        assertEquals("Format", searchQueryResult2.format());
        assertEquals("Track", searchQueryResult2.track());
        assertEquals(DiscogCountries.UK, searchQueryResult2.country());
        assertEquals(DiscogsTypes.RELEASE, searchQueryResult2.types());
        assertSame(searchQuery, searchQueryResult);
    }

    /**
     * Test {@link MappingService#mapResultsToDTO(List)}.
     *
     * <ul>
     *   <li>Then return second is first.
     * </ul>
     *
     * <p>Method under test: {@link MappingService#mapResultsToDTO(List)}
     */
    @Test
    @DisplayName("Test mapResultsToDTO(List); then return second is first")
    void testMapResultsToDTO_thenReturnSecondIsFirst() {
        // Arrange
        ArrayList<DiscogsResultDTO> resultDTOList = new ArrayList<>();
        DiscogsQueryDTO searchQuery =
                new DiscogsQueryDTO(
                        "Artist",
                        "Album",
                        "Track",
                        "Dr",
                        "Format",
                        DiscogCountries.UK,
                        DiscogsTypes.RELEASE,
                        "Barcode");

        resultDTOList.add(new DiscogsResultDTO(searchQuery, new ArrayList<>()));
        DiscogsQueryDTO searchQuery2 =
                new DiscogsQueryDTO(
                        "Artist",
                        "Album",
                        "Track",
                        "Dr",
                        "Format",
                        DiscogCountries.UK,
                        DiscogsTypes.RELEASE,
                        "Barcode");

        resultDTOList.add(new DiscogsResultDTO(searchQuery2, new ArrayList<>()));

        // Act
        List<DiscogsMapResultDTO> actualMapResultsToDTOResult =
                mappingService.mapResultsToDTO(resultDTOList);

        // Assert
        assertEquals(2, actualMapResultsToDTOResult.size());
        DiscogsMapResultDTO getResult = actualMapResultsToDTOResult.getFirst();
        DiscogsQueryDTO searchQueryResult = getResult.searchQuery();
        assertEquals("Dr", searchQueryResult.title());
        assertNull(getResult.cheapestItem());
        assertTrue(getResult.results().isEmpty());
        assertEquals(getResult, actualMapResultsToDTOResult.get(1));
        assertSame(searchQuery, searchQueryResult);
    }

    /**
     * Test {@link MappingService#mapResultsToDTO(List)}.
     *
     * <ul>
     *   <li>Then return size is one.
     * </ul>
     *
     * <p>Method under test: {@link MappingService#mapResultsToDTO(List)}
     */
    @Test
    @DisplayName("Test mapResultsToDTO(List); then return size is one")
    void testMapResultsToDTO_thenReturnSizeIsOne() {
        // Arrange
        ArrayList<DiscogsResultDTO> resultDTOList = new ArrayList<>();
        DiscogsQueryDTO searchQuery =
                new DiscogsQueryDTO(
                        "Artist",
                        "Album",
                        "Track",
                        "Dr",
                        "Format",
                        DiscogCountries.UK,
                        DiscogsTypes.RELEASE,
                        "Barcode");

        resultDTOList.add(new DiscogsResultDTO(searchQuery, new ArrayList<>()));

        // Act
        List<DiscogsMapResultDTO> actualMapResultsToDTOResult =
                mappingService.mapResultsToDTO(resultDTOList);

        // Assert
        assertEquals(1, actualMapResultsToDTOResult.size());
        DiscogsMapResultDTO getResult = actualMapResultsToDTOResult.getFirst();
        DiscogsQueryDTO searchQueryResult = getResult.searchQuery();
        assertEquals("Dr", searchQueryResult.title());
        assertNull(getResult.cheapestItem());
        assertTrue(getResult.results().isEmpty());
        assertSame(searchQuery, searchQueryResult);
    }

    /**
     * Test {@link MappingService#mapResultsToDTO(List)}.
     *
     * <ul>
     *   <li>When {@link ArrayList#ArrayList()}.
     *   <li>Then return Empty.
     * </ul>
     *
     * <p>Method under test: {@link MappingService#mapResultsToDTO(List)}
     */
    @Test
    @DisplayName("Test mapResultsToDTO(List); when ArrayList(); then return Empty")
    void testMapResultsToDTO_whenArrayList_thenReturnEmpty() {
        // Arrange, Act and Assert
        assertTrue(mappingService.mapResultsToDTO(new ArrayList<>()).isEmpty());
    }

    /**
     * Test {@link MappingService#mapObjectToDTO(DiscogsResult, DiscogsQueryDTO)}.
     *
     * <ul>
     *   <li>Given {@link ArrayList#ArrayList()} add {@code foo}.
     *   <li>Then return results first format size is one.
     * </ul>
     *
     * <p>Method under test: {@link MappingService#mapObjectToDTO(DiscogsResult, DiscogsQueryDTO)}
     */
    @Test
    @DisplayName(
            "Test mapObjectToDTO(DiscogsResult, DiscogsQueryDTO); given ArrayList() add 'foo'; then"
                    + " return results first format size is one")
    void testMapObjectToDTO_givenArrayListAddFoo_thenReturnResultsFirstFormatSizeIsOne() {
        // Arrange
        ArrayList<String> format = new ArrayList<>();
        format.add("foo");
        DiscogsEntry buildResult =
                DiscogsEntry.builder()
                        .country("GB")
                        .format(format)
                        .id(1)
                        .lowestPrice(10.0f)
                        .numberForSale(10)
                        .title("Dr")
                        .uri("Uri")
                        .url("https://example.org/example")
                        .year("Year")
                        .build();

        ArrayList<DiscogsEntry> discogsEntryList = new ArrayList<>();
        discogsEntryList.add(buildResult);
        DiscogsEntry.DiscogsEntryBuilder countryResult = DiscogsEntry.builder().country("GB");
        DiscogsEntry buildResult2 =
                countryResult
                        .format(new ArrayList<>())
                        .id(1)
                        .lowestPrice(10.0f)
                        .numberForSale(10)
                        .title("Dr")
                        .uri("Uri")
                        .url("https://example.org/example")
                        .year("Year")
                        .build();
        discogsEntryList.add(buildResult2);
        DiscogsEntry.DiscogsEntryBuilder countryResult2 = DiscogsEntry.builder().country("GB");
        DiscogsEntry buildResult3 =
                countryResult2
                        .format(new ArrayList<>())
                        .id(1)
                        .lowestPrice(10.0f)
                        .numberForSale(10)
                        .title("Dr")
                        .uri("Uri")
                        .url("https://example.org/example")
                        .year("Year")
                        .build();
        discogsEntryList.add(buildResult3);
        DiscogsResult discogsResult = mock(DiscogsResult.class);
        when(discogsResult.getResults()).thenReturn(discogsEntryList);

        // Act
        DiscogsResultDTO actualMapObjectToDTOResult =
                mappingService.mapObjectToDTO(
                        discogsResult,
                        new DiscogsQueryDTO(
                                "Artist",
                                "Album",
                                "Track",
                                "Dr",
                                "Format",
                                DiscogCountries.UK,
                                DiscogsTypes.RELEASE,
                                "Barcode"));

        // Assert
        verify(discogsResult).getResults();
        List<DiscogsEntryDTO> resultsResult = actualMapObjectToDTOResult.results();
        assertEquals(3, resultsResult.size());
        List<String> formatResult = resultsResult.getFirst().format();
        assertEquals(1, formatResult.size());
        assertEquals("foo", formatResult.getFirst());
        assertEquals(resultsResult.get(1), resultsResult.get(2));
        assertSame(format, formatResult);
    }

    /**
     * Test {@link MappingService#mapObjectToDTO(DiscogsResult, DiscogsQueryDTO)}.
     *
     * <ul>
     *   <li>Given {@link ArrayList#ArrayList()}.
     *   <li>Then return results Empty.
     * </ul>
     *
     * <p>Method under test: {@link MappingService#mapObjectToDTO(DiscogsResult, DiscogsQueryDTO)}
     */
    @Test
    @DisplayName(
            "Test mapObjectToDTO(DiscogsResult, DiscogsQueryDTO); given ArrayList(); then return results"
                    + " Empty")
    void testMapObjectToDTO_givenArrayList_thenReturnResultsEmpty() {
        // Arrange
        DiscogsResult discogsResult = mock(DiscogsResult.class);
        when(discogsResult.getResults()).thenReturn(new ArrayList<>());
        DiscogsQueryDTO discogsQueryDTO =
                new DiscogsQueryDTO(
                        "Artist",
                        "Album",
                        "Track",
                        "Dr",
                        "Format",
                        DiscogCountries.UK,
                        DiscogsTypes.RELEASE,
                        "Barcode");

        // Act
        DiscogsResultDTO actualMapObjectToDTOResult =
                mappingService.mapObjectToDTO(discogsResult, discogsQueryDTO);

        // Assert
        verify(discogsResult).getResults();
        assertTrue(actualMapObjectToDTOResult.results().isEmpty());
        assertSame(discogsQueryDTO, actualMapObjectToDTOResult.searchQuery());
    }

    /**
     * Test {@link MappingService#mapObjectToDTO(DiscogsResult, DiscogsQueryDTO)}.
     *
     * <ul>
     *   <li>Then return results size is one.
     * </ul>
     *
     * <p>Method under test: {@link MappingService#mapObjectToDTO(DiscogsResult, DiscogsQueryDTO)}
     */
    @Test
    @DisplayName(
            "Test mapObjectToDTO(DiscogsResult, DiscogsQueryDTO); then return results size is one")
    void testMapObjectToDTO_thenReturnResultsSizeIsOne() {
        // Arrange
        ArrayList<DiscogsEntry> discogsEntryList = new ArrayList<>();
        DiscogsEntry.DiscogsEntryBuilder countryResult = DiscogsEntry.builder().country("GB");
        DiscogsEntry buildResult =
                countryResult
                        .format(new ArrayList<>())
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

        // Act
        DiscogsResultDTO actualMapObjectToDTOResult =
                mappingService.mapObjectToDTO(
                        discogsResult,
                        new DiscogsQueryDTO(
                                "Artist",
                                "Album",
                                "Track",
                                "Dr",
                                "Format",
                                DiscogCountries.UK,
                                DiscogsTypes.RELEASE,
                                "Barcode"));

        // Assert
        verify(discogsResult).getResults();
        List<DiscogsEntryDTO> resultsResult = actualMapObjectToDTOResult.results();
        assertEquals(1, resultsResult.size());
        DiscogsEntryDTO getResult = resultsResult.getFirst();
        assertEquals("Dr", getResult.title());
        assertEquals("GB", getResult.country());
        assertEquals("Uri", getResult.uri());
        assertEquals("Year", getResult.year());
        assertEquals("https://example.org/example", getResult.url());
        assertNull(getResult.isOnMarketplace());
        assertEquals(1, getResult.id());
        assertEquals(10, getResult.numberForSale().intValue());
        assertEquals(10.0f, getResult.lowestPrice().floatValue());
        assertTrue(getResult.format().isEmpty());
    }

    /**
     * Test {@link MappingService#mapObjectToDTO(DiscogsResult, DiscogsQueryDTO)}.
     *
     * <ul>
     *   <li>Then return results size is two.
     * </ul>
     *
     * <p>Method under test: {@link MappingService#mapObjectToDTO(DiscogsResult, DiscogsQueryDTO)}
     */
    @Test
    @DisplayName(
            "Test mapObjectToDTO(DiscogsResult, DiscogsQueryDTO); then return results size is two")
    void testMapObjectToDTO_thenReturnResultsSizeIsTwo() {
        // Arrange
        ArrayList<DiscogsEntry> discogsEntryList = new ArrayList<>();
        DiscogsEntry.DiscogsEntryBuilder countryResult = DiscogsEntry.builder().country("GB");
        DiscogsEntry buildResult =
                countryResult
                        .format(new ArrayList<>())
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
        DiscogsEntry buildResult2 =
                countryResult2
                        .format(new ArrayList<>())
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

        // Act
        DiscogsResultDTO actualMapObjectToDTOResult =
                mappingService.mapObjectToDTO(
                        discogsResult,
                        new DiscogsQueryDTO(
                                "Artist",
                                "Album",
                                "Track",
                                "Dr",
                                "Format",
                                DiscogCountries.UK,
                                DiscogsTypes.RELEASE,
                                "Barcode"));

        // Assert
        verify(discogsResult).getResults();
        List<DiscogsEntryDTO> resultsResult = actualMapObjectToDTOResult.results();
        assertEquals(2, resultsResult.size());
        DiscogsEntryDTO getResult = resultsResult.get(0);
        assertTrue(getResult.format().isEmpty());
        assertEquals(getResult, resultsResult.get(1));
    }

    /**
     * Test {@link MappingService#mapObjectToDTO(DiscogsResult, DiscogsQueryDTO)}.
     *
     * <ul>
     *   <li>Then return results third is results first.
     * </ul>
     *
     * <p>Method under test: {@link MappingService#mapObjectToDTO(DiscogsResult, DiscogsQueryDTO)}
     */
    @Test
    @DisplayName(
            "Test mapObjectToDTO(DiscogsResult, DiscogsQueryDTO); then return results third is results"
                    + " first")
    void testMapObjectToDTO_thenReturnResultsThirdIsResultsFirst() {
        // Arrange
        ArrayList<DiscogsEntry> discogsEntryList = new ArrayList<>();
        DiscogsEntry.DiscogsEntryBuilder countryResult = DiscogsEntry.builder().country("GB");
        DiscogsEntry buildResult =
                countryResult
                        .format(new ArrayList<>())
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
        DiscogsEntry buildResult2 =
                countryResult2
                        .format(new ArrayList<>())
                        .id(1)
                        .lowestPrice(10.0f)
                        .numberForSale(10)
                        .title("Dr")
                        .uri("Uri")
                        .url("https://example.org/example")
                        .year("Year")
                        .build();
        discogsEntryList.add(buildResult2);
        DiscogsEntry.DiscogsEntryBuilder countryResult3 = DiscogsEntry.builder().country("GB");
        DiscogsEntry buildResult3 =
                countryResult3
                        .format(new ArrayList<>())
                        .id(1)
                        .lowestPrice(10.0f)
                        .numberForSale(10)
                        .title("Dr")
                        .uri("Uri")
                        .url("https://example.org/example")
                        .year("Year")
                        .build();
        discogsEntryList.add(buildResult3);
        DiscogsResult discogsResult = mock(DiscogsResult.class);
        when(discogsResult.getResults()).thenReturn(discogsEntryList);

        // Act
        DiscogsResultDTO actualMapObjectToDTOResult =
                mappingService.mapObjectToDTO(
                        discogsResult,
                        new DiscogsQueryDTO(
                                "Artist",
                                "Album",
                                "Track",
                                "Dr",
                                "Format",
                                DiscogCountries.UK,
                                DiscogsTypes.RELEASE,
                                "Barcode"));

        // Assert
        verify(discogsResult).getResults();
        List<DiscogsEntryDTO> resultsResult = actualMapObjectToDTOResult.results();
        assertEquals(3, resultsResult.size());
        DiscogsEntryDTO getResult = resultsResult.get(0);
        assertEquals(getResult, resultsResult.get(1));
        assertEquals(getResult, resultsResult.get(2));
    }

    /**
     * Test {@link MappingService#mapObjectToDTO(DiscogsResult, DiscogsQueryDTO)}.
     *
     * <ul>
     *   <li>When builder results {@link ArrayList#ArrayList()} build.
     *   <li>Then return results Empty.
     * </ul>
     *
     * <p>Method under test: {@link MappingService#mapObjectToDTO(DiscogsResult, DiscogsQueryDTO)}
     */
    @Test
    @DisplayName(
            "Test mapObjectToDTO(DiscogsResult, DiscogsQueryDTO); when builder results ArrayList() build;"
                    + " then return results Empty")
    void testMapObjectToDTO_whenBuilderResultsArrayListBuild_thenReturnResultsEmpty() {
        // Arrange
        DiscogsResult.DiscogsResultBuilder builderResult = DiscogsResult.builder();
        DiscogsResult discogsResult = builderResult.results(new ArrayList<>()).build();
        DiscogsQueryDTO discogsQueryDTO =
                new DiscogsQueryDTO(
                        "Artist",
                        "Album",
                        "Track",
                        "Dr",
                        "Format",
                        DiscogCountries.UK,
                        DiscogsTypes.RELEASE,
                        "Barcode");

        // Act
        DiscogsResultDTO actualMapObjectToDTOResult =
                mappingService.mapObjectToDTO(discogsResult, discogsQueryDTO);

        // Assert
        assertTrue(actualMapObjectToDTOResult.results().isEmpty());
        assertSame(discogsQueryDTO, actualMapObjectToDTOResult.searchQuery());
    }
}
