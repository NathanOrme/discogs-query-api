package org.discogs.query.service;

import org.discogs.query.model.DiscogsQueryDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DefaultDiscogsQueryServiceIT {

    @Autowired
    private DiscogsQueryService discogsQueryService;

    @Test
    void search_WithKnownQuery_ReturnsResult() {
        DiscogsQueryDTO discogsQueryDTO = DiscogsQueryDTO.builder()
                .artist("Jimi Hendrix")
                .track("All Along The Watchtower")
                .build();
        var result = discogsQueryService.searchBasedOnQuery(discogsQueryDTO);
        assertNotNull(result);
    }
}
