package org.discogs.query.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.discogs.query.config.SecurityConfig;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.discogs.query.service.QueryProcessingService;
import org.discogs.query.service.ResultCalculationService;
import org.discogs.query.service.utils.MappingService;
import org.junit.jupiter.api.Disabled;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Disabled
@Import(SecurityConfig.class)
@WebMvcTest(DiscogsQueryController.class)
class DiscogsQueryControllerTest {

    private static final String BASIC_AUTH_HEADER =
            "Basic " + java.util.Base64.getEncoder().encodeToString(("username:password").getBytes());

    private static final DiscogsQueryDTO DISCOGS_QUERY_DTO = new DiscogsQueryDTO(
            "War", // artist
            null,  // album
            "Low Rider", // track
            null,  // format
            null,  // country
            null,  // types
            null   // barcode
    );

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QueryProcessingService queryProcessingService;

    @MockBean
    private MappingService mappingService;

    @MockBean
    private ResultCalculationService resultCalculationService;

    @Test
    void testSearchBasedOnQuery_Successful() throws Exception {
        // Arrange
        DiscogsResultDTO resultDTO = new DiscogsResultDTO(DISCOGS_QUERY_DTO, List.of());
        DiscogsMapResultDTO mapResultDTO = new DiscogsMapResultDTO(DISCOGS_QUERY_DTO, Map.of());

        List<DiscogsResultDTO> resultDTOList = List.of(resultDTO);
        List<DiscogsMapResultDTO> mapResultDTOList = List.of(mapResultDTO);

        when(queryProcessingService.processQueries(anyList()))
                .thenReturn(resultDTOList);
        when(resultCalculationService.calculateSizeOfResults(resultDTOList))
                .thenReturn(resultDTOList.size());
        when(mappingService.mapResultsToDTO(resultDTOList))
                .thenReturn(mapResultDTOList);

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
        when(queryProcessingService.processQueries(anyList()))
                .thenReturn(List.of());
        when(resultCalculationService.calculateSizeOfResults(anyList()))
                .thenReturn(0);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/discogs-query/search")
                        .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(List.of(DISCOGS_QUERY_DTO))))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(print());
    }

    @Test
    void testSearchBasedOnQuery_Exception() throws Exception {
        // Arrange
        when(queryProcessingService.processQueries(anyList()))
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
