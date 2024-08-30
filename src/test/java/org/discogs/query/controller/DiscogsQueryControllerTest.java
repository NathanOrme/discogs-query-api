package org.discogs.query.controller;

import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.discogs.query.service.DiscogsQueryService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiscogsQueryController.class)
class DiscogsQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiscogsQueryService discogsQueryService;
    private static final String BASIC_AUTH_HEADER = "Basic " + java.util.Base64.getEncoder().encodeToString("username:password".getBytes());


    @Test
    void testSearchDiscogs_Success() throws Exception {
        // Arrange
        DiscogsResultDTO resultDTO = new DiscogsResultDTO();  // You would populate this with the actual expected result data

        // Mock the service to return the resultDTO when called
        when(discogsQueryService.searchBasedOnQuery(any(DiscogsQueryDTO.class)))
                .thenReturn(resultDTO);

        // Act & Assert
        mockMvc.perform(get("/discogs-query/search")
                        .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"artist\":\"The Beatles\", \"track\":\"Hey Jude\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @Disabled
    void testSearchDiscogs_InvalidRequest() throws Exception {
        // Here you can simulate invalid input and expect a 400 status, etc.
        mockMvc.perform(get("/discogs-query/search")
                        .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"artist\":\"\", \"track\":\"\"}"))  // Invalid input
                .andExpect(status().isBadRequest());
    }

}