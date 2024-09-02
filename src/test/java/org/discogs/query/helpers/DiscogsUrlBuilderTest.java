package org.discogs.query.helpers;

import org.discogs.query.domain.DiscogsEntry;
import org.discogs.query.model.DiscogsQueryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link DiscogsUrlBuilder}.
 */
@SpringBootTest
class DiscogsUrlBuilderTest {

    private DiscogsUrlBuilder discogsUrlBuilder;

    @Value("${discogs.url}")
    private String discogsBaseUrl;

    @Value("${discogs.search}")
    private String discogsSearchEndpoint;

    @Value("${discogs.release}")
    private String releaseEndpoint;

    @Value("${discogs.page-size}")
    private int pageSize;

    @Value("${discogs.token}")
    private String token;

    @Value("${discogs.baseUrl}")
    private String discogsWebsiteBaseUrl;

    @BeforeEach
    public void setUp() {
        discogsUrlBuilder = new DiscogsUrlBuilder(new UriBuilderHelper());
        discogsUrlBuilder.discogsBaseUrl = discogsBaseUrl;
        discogsUrlBuilder.discogsSearchEndpoint = discogsSearchEndpoint;
        discogsUrlBuilder.releaseEndpoint = releaseEndpoint;
        discogsUrlBuilder.pageSize = pageSize;
        discogsUrlBuilder.token = token;
        discogsUrlBuilder.discogsWebsiteBaseUrl = discogsWebsiteBaseUrl;
    }

    /**
     * Tests URL construction for search queries.
     */
    @Test
    void testBuildSearchUrl() {
        DiscogsQueryDTO queryDTO = new DiscogsQueryDTO();
        queryDTO.setArtist("Artist A");
        queryDTO.setTrack("Track A");
        queryDTO.setAlbum("Album A");
        queryDTO.setFormat("Format A");

        String actualUrl = discogsUrlBuilder.buildSearchUrl(queryDTO);
        assertNotNull(actualUrl);
    }

    /**
     * Tests URL construction for release endpoints.
     */
    @Test
    void testBuildReleaseUrl() {
        DiscogsEntry entry = new DiscogsEntry();
        entry.setId(123);

        String expectedUrl = discogsBaseUrl.concat(releaseEndpoint)
                .concat("123")
                .concat("?token=").concat(token)
                .concat("&curr_abbr=").concat("GBP");

        String actualUrl = discogsUrlBuilder.buildReleaseUrl(entry);
        assertEquals(expectedUrl, actualUrl);
    }
}

