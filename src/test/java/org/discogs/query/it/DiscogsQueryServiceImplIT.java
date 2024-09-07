package org.discogs.query.it;

import lombok.extern.slf4j.Slf4j;
import org.discogs.query.enums.DiscogCountries;
import org.discogs.query.enums.DiscogsFormats;
import org.discogs.query.interfaces.DiscogsQueryService;
import org.discogs.query.model.DiscogsQueryDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
class DiscogsQueryServiceImplIT {

    @Autowired
    private DiscogsQueryService discogsQueryService;

    @Test
    void search_WithKnownQuery_ReturnsResult() {
        DiscogsQueryDTO discogsQueryDTO = DiscogsQueryDTO.builder()
                .artist("Jimi Hendrix")
                .track("All Along The Watchtower")
                .format(DiscogsFormats.VINYL.getFormat())
                .country(DiscogCountries.UK)
                .build();
        var result = discogsQueryService.searchBasedOnQuery(discogsQueryDTO);
        assertNotNull(result);
        assertNotEquals(0, result.getResults().size());
        log.info(result.toString());
    }

    @Test
    void search_WithKnownQueryAndBarcode_ReturnsResult() {
        DiscogsQueryDTO discogsQueryDTO = DiscogsQueryDTO.builder()
                .barcode("7 2064-24425-2 4")
                .build();
        var result = discogsQueryService.searchBasedOnQuery(discogsQueryDTO);
        assertNotNull(result);
        assertNotEquals(0, result.getResults().size());
        log.info(result.toString());
    }

    @Test
    void search_WithKnownQueryAndCompilationVinyl_ReturnsResult() {
        DiscogsQueryDTO discogsQueryDTO = DiscogsQueryDTO.builder()
                .artist("Sam Cooke")
                .track("Chain Gang")
                .format(DiscogsFormats.LP.getFormat())
                .country(DiscogCountries.UK)
                .build();
        var result = discogsQueryService.searchBasedOnQuery(discogsQueryDTO);
        assertNotNull(result);
        assertNotEquals(0, result.getResults().size());
        log.info(result.toString());
    }
}
