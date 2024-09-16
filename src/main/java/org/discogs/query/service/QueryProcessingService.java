package org.discogs.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.interfaces.DiscogsQueryService;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.discogs.query.service.utils.CompletableFutureService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueryProcessingService {

    private final DiscogsQueryService discogsQueryService;
    private final CompletableFutureService completableFutureService;

    public List<DiscogsResultDTO> processQueries(final List<DiscogsQueryDTO> discogsQueryDTOList) {
        List<CompletableFuture<DiscogsResultDTO>> futures = createFuturesForQueries(discogsQueryDTOList);
        return completableFutureService.processFuturesWithTimeout(futures);
    }

    private List<CompletableFuture<DiscogsResultDTO>> createFuturesForQueries(
            final List<DiscogsQueryDTO> discogsQueryDTOList) {
        return discogsQueryDTOList.stream()
                .map(query -> CompletableFuture.supplyAsync(() -> {
                    log.debug("Processing query: {}", query);
                    return discogsQueryService.searchBasedOnQuery(query);
                }))
                .toList();
    }
}
