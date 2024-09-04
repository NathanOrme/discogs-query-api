package org.discogs.query.controller;

import org.discogs.query.config.SecurityConfig;
import org.discogs.query.interfaces.DiscogsQueryService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(DiscogsQueryController.class)
class DiscogsQueryControllerTest {

    private static final String BASIC_AUTH_HEADER =
            "Basic " + java.util.Base64.getEncoder().encodeToString((
                    "username" +
                    ":password").getBytes());
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DiscogsQueryService discogsQueryService;

    @Test
    void testSearchDiscogs_Success() throws Exception {
        // Arrange
        DiscogsResultDTO resultDTO = new DiscogsResultDTO();  // You would
        // populate this with the actual expected result data

        // Mock the service to return the resultDTO when called
        when(discogsQueryService.searchBasedOnQuery(any(DiscogsQueryDTO.class)))
                .thenReturn(resultDTO);

        // Act & Assert
        mockMvc.perform(post("/discogs-query/search")
                        .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"artist\":\"The Beatles\", " +
                                "\"track\":\"Hey Jude\"}]"))
                .andExpect(status().isOk());
    }

}