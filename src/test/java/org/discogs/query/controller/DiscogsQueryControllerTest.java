package org.discogs.query.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.discogs.query.interfaces.CollectionsService;
import org.discogs.query.interfaces.DiscogsQueryService;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(DiscogsQueryController.class)
public class DiscogsQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private DiscogsQueryService discogsQueryService;

    @Mock
    private CollectionsService collectionsService;

    @InjectMocks
    private DiscogsQueryController discogsQueryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchBasedOnQuery_Successful() throws Exception {
        // Given
        DiscogsQueryDTO queryDTO = new DiscogsQueryDTO();
        DiscogsResultDTO resultDTO = new DiscogsResultDTO();
        DiscogsMapResultDTO mapResultDTO = new DiscogsMapResultDTO();

        List<DiscogsResultDTO> resultDTOList = List.of(resultDTO);
        List<DiscogsMapResultDTO> mapResultDTOList = List.of(mapResultDTO);

        when(discogsQueryService.searchBasedOnQuery(any(DiscogsQueryDTO.class))).thenReturn(resultDTO);
        when(collectionsService.convertListToMapForDTO(any(DiscogsResultDTO.class))).thenReturn(mapResultDTO);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/discogs-query/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(List.of(queryDTO))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").isNotEmpty())
                .andDo(print());
    }

    @Test
    void testSearchBasedOnQuery_NoResults() throws Exception {
        // Given
        DiscogsQueryDTO queryDTO = new DiscogsQueryDTO();
        when(discogsQueryService.searchBasedOnQuery(any(DiscogsQueryDTO.class))).thenReturn(null);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/discogs-query/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(List.of(queryDTO))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty())
                .andDo(print());
    }

    @Test
    void testSearchBasedOnQuery_Exception() throws Exception {
        // Given
        DiscogsQueryDTO queryDTO = new DiscogsQueryDTO();
        when(discogsQueryService.searchBasedOnQuery(any(DiscogsQueryDTO.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/discogs-query/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(List.of(queryDTO))))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty())
                .andDo(print());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
