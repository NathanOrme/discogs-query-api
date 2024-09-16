package org.discogs.query.service.discogs;

import org.discogs.query.domain.DiscogsEntry;
import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.exceptions.DiscogsSearchException;
import org.discogs.query.helpers.DiscogsUrlBuilder;
import org.discogs.query.interfaces.DiscogsAPIClient;
import org.discogs.query.interfaces.DiscogsFilterService;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.discogs.query.model.enums.DiscogsFormats;
import org.discogs.query.service.MappingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscogsQueryServiceImplTest {

    @Mock
    private DiscogsAPIClient discogsAPIClient;

    @Mock
    private MappingService mappingService;

    @Mock
    private DiscogsUrlBuilder discogsUrlBuilder;

    @Mock
    private DiscogsFilterService discogsFilterService;

    @InjectMocks
    private DiscogsQueryServiceImpl discogsQueryServiceImpl;

    private DiscogsQueryDTO discogsQueryDTO;

    @BeforeEach
    void setUp() {
        // Initialize test data
        discogsQueryDTO = new DiscogsQueryDTO("Test Artist", null, "Test Track",
                DiscogsFormats.COMP.getFormat(), null, null, null);

        DiscogsEntry entry = new DiscogsEntry();
        entry.setUri("/test-uri");
        DiscogsResult discogsResult = new DiscogsResult();
        discogsResult.setResults(Collections.singletonList(entry));

    }

    @Test
    void testSearchBasedOnQuery_discogsSearchException() {
        // Setup mock behaviors
        when(discogsUrlBuilder.buildSearchUrl(discogsQueryDTO)).thenReturn(
                "mocked-url");
        doThrow(new DiscogsSearchException("API error")).when(discogsAPIClient).getResultsForQuery(anyString());

        // Perform the search
        DiscogsResultDTO result =
                discogsQueryServiceImpl.searchBasedOnQuery(discogsQueryDTO);

        // Verify behaviors
        verify(discogsAPIClient, times(1)).getResultsForQuery(anyString());

        // Validate results
        assertEquals(new DiscogsResultDTO(null, null).toString(), result.toString());
        // Empty DTO is expected on exception
    }

    @Test
    void testSearchBasedOnQuery_unexpectedException() {
        // Setup mock behaviors
        when(discogsUrlBuilder.buildSearchUrl(discogsQueryDTO)).thenReturn(
                "mocked-url");
        doThrow(new RuntimeException("Unexpected error")).when(discogsAPIClient).getResultsForQuery(anyString());

        // Perform the search and expect exception
        assertThrows(DiscogsSearchException.class, () -> {
            discogsQueryServiceImpl.searchBasedOnQuery(discogsQueryDTO);
        });

        // Verify behaviors
        verify(discogsAPIClient, times(1)).getResultsForQuery(anyString());
    }

    @Test
    void testIsCompilationFormat() {

        // When
        boolean isCompilation =
                DiscogsQueryServiceImpl.isCompilationFormat(discogsQueryDTO);

        // Then
        assertTrue(isCompilation);
    }
}