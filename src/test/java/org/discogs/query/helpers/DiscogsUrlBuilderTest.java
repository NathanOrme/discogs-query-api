package org.discogs.query.helpers;

import org.discogs.query.domain.api.DiscogsEntry;
import org.discogs.query.model.DiscogsQueryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link DiscogsUrlBuilder}.
 */
@SpringBootTest
class DiscogsUrlBuilderTest {

    // Hardcoded values for testing
    private final String discogsBaseUrl = "https://api.discogs.com";
    private final String releaseEndpoint = "/releases/";
    private final String token = "dummyToken";
    private DiscogsUrlBuilder discogsUrlBuilder;

    @BeforeEach
    public void setUp() {
        buildDiscogsUriBuilder();
        discogsUrlBuilder.discogsBaseUrl = discogsBaseUrl;
        discogsUrlBuilder.discogsSearchEndpoint = "/search";
        discogsUrlBuilder.releaseEndpoint = releaseEndpoint;
        discogsUrlBuilder.pageSize = 50;
        discogsUrlBuilder.token = token;
        discogsUrlBuilder.discogsWebsiteBaseUrl = "https://www.discogs.com";
    }

    private void buildDiscogsUriBuilder() {
        StringHelper stringHelper = new StringHelper();
        UriBuilderHelper uriBuilderHelper = new UriBuilderHelper(stringHelper);
        discogsUrlBuilder = new DiscogsUrlBuilder(uriBuilderHelper, stringHelper);
    }

    /**
     * Tests URL construction for search queries.
     */
    @Test
    void testBuildSearchUrl() {
        DiscogsQueryDTO queryDTO = new DiscogsQueryDTO(
                "Artist A", // artist
                "Album A",  // album
                "Track A",  // track
                null,
                "Format A", // format
                null,       // country
                null,       // types
                null        // barcode
        );

        String actualUrl = discogsUrlBuilder.buildSearchUrl(queryDTO);
        assertNotNull(actualUrl);
        // Add more assertions here to validate the constructed URL
    }

    /**
     * Tests URL construction for release endpoints.
     */
    @Test
    void testBuildReleaseUrl() {
        DiscogsEntry entry = DiscogsEntry.builder()
                .id(123)
                .build();

        String expectedUrl = discogsBaseUrl.concat(releaseEndpoint)
                .concat("123")
                .concat("?token=").concat(token)
                .concat("&curr_abbr=").concat("GBP");

        String actualUrl = discogsUrlBuilder.buildReleaseUrl(entry);
        assertEquals(expectedUrl, actualUrl);
    }
}
