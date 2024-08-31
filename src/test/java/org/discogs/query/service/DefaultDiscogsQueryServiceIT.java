package org.discogs.query.service;

import lombok.extern.slf4j.Slf4j;
import org.discogs.query.enums.DiscogsFormats;
import org.discogs.query.interfaces.DiscogsQueryService;
import org.discogs.query.model.DiscogsQueryDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
class DefaultDiscogsQueryServiceIT {

    @Autowired
    private DiscogsQueryService discogsQueryService;

    @Test
    void search_WithKnownQuery_ReturnsResult() {
        DiscogsQueryDTO discogsQueryDTO = DiscogsQueryDTO.builder()
                .artist("Jimi Hendrix")
                .track("All Along The Watchtower")
                .format(DiscogsFormats.VINYL_COMPILATION.getFormat())
                .build();
        var result = discogsQueryService.searchBasedOnQuery(discogsQueryDTO);
        assertNotNull(result);
        log.info(result.toString());
    }
}
