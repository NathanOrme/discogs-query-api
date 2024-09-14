package org.discogs.query.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.discogs.query.config.SecurityConfig;
import org.discogs.query.interfaces.DiscogsMappingService;
import org.discogs.query.interfaces.DiscogsQueryService;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Import(SecurityConfig.class)
@WebMvcTest(DiscogsQueryController.class)
class DiscogsQueryControllerTest {

    private static final String BASIC_AUTH_HEADER =
            "Basic " + java.util.Base64.getEncoder().encodeToString((
                    "username" +
                            ":password").getBytes());

    private static final DiscogsQueryDTO DISCOGS_QUERY_DTO = DiscogsQueryDTO.builder()
            .artist("War")
            .track("Low Rider")
            .build();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiscogsQueryService discogsQueryService;

    @MockBean
    private DiscogsMappingService discogsMappingService;

    @Test
    void testSearchBasedOnQuery_Successful() throws Exception {
        // Arrange
        DiscogsResultDTO resultDTO = new DiscogsResultDTO();
        DiscogsMapResultDTO mapResultDTO = new DiscogsMapResultDTO();

        List<DiscogsResultDTO> resultDTOList = List.of(resultDTO);
        List<DiscogsMapResultDTO> mapResultDTOList = List.of(mapResultDTO);

        when(discogsQueryService.searchBasedOnQuery(any(DiscogsQueryDTO.class))).thenReturn(resultDTO);
        when(discogsMappingService.convertEntriesToMapByTitle(any(DiscogsResultDTO.class))).thenReturn(mapResultDTO);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/discogs-query/search")
                        .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(List.of(DISCOGS_QUERY_DTO))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists())
                .andDo(print());
    }

    @Test
    void testSearchBasedOnQuery_NoResults() throws Exception {
        // Arrange
        when(discogsQueryService.searchBasedOnQuery(any(DiscogsQueryDTO.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/discogs-query/search")
                        .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(List.of(DISCOGS_QUERY_DTO))))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void testSearchBasedOnQuery_Exception() throws Exception {
        // Arrange
        when(discogsQueryService.searchBasedOnQuery(any(DiscogsQueryDTO.class)))
                .thenThrow(new RuntimeException("Test Exception"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/discogs-query/search")
                        .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(List.of(DISCOGS_QUERY_DTO))))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andDo(print());
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
