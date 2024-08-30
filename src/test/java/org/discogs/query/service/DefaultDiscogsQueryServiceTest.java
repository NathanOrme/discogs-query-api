package org.discogs.query.service;

import org.discogs.query.model.DiscogsQueryDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DefaultDiscogsQueryServiceTest {
    @Autowired
    private DiscogsQueryService discogsQueryService;

    @Test
    void search_WithKnownQuery_ReturnsResult(){
        DiscogsQueryDTO discogsQueryDTO = DiscogsQueryDTO.builder()
                .artist("Jimi Hendrix")
                .track("All Along The Watchtower")
                .build();
        discogsQueryService.searchBasedOnQuery(discogsQueryDTO);
    }
}
