package org.discogs.query.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.interfaces.DiscogsQueryService;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.discogs.query.service.CollectionsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Controller for handling Discogs query-related operations.
 * This controller provides an API endpoint for searching Discogs based on
 * user queries.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("discogs-query")
public class DiscogsQueryController {

    private final DiscogsQueryService discogsQueryService;
    private final CollectionsService collectionsService;

    /**
     * Searches Discogs using the provided query data.
     *
     * @param discogsQueryDTO the data transfer object containing the search
     *                        query details
     * @return a {@link ResponseEntity} containing the search results wrapped
     * in {@link DiscogsResultDTO}
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiscogsMapResultDTO>> searchBasedOnQuery(
            @RequestBody @Valid final List<DiscogsQueryDTO> discogsQueryDTO) {

        log.info("Received search request with {} queries",
                discogsQueryDTO.size());

        List<DiscogsResultDTO> resultDTOList = discogsQueryDTO.stream()
                .map(query -> {
                    log.debug("Processing query: {}", query);
                    return discogsQueryService.searchBasedOnQuery(query);
                })
                .filter(Objects::nonNull)
                .peek(result -> log.debug("Received result: {}", result))
                .toList();

        if (resultDTOList.isEmpty()) {
            log.warn("No results found for the provided queries");
            return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());
        }
        int size = calculateSizeOfResults(resultDTOList);
        log.info("Returning {} results: {}", size, resultDTOList);
        var resultMapDTOList = resultDTOList.parallelStream()
                .map(entry -> collectionsService.convertListToMapForDTO(entry))
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(resultMapDTOList);
    }

    private int calculateSizeOfResults(final List<DiscogsResultDTO> resultDTOList) {
        return resultDTOList.parallelStream()
                .filter(discogsResultDTO -> discogsResultDTO.getResults() != null)
                .mapToInt(discogsResultDTO -> discogsResultDTO.getResults().size())
                .sum();
    }

}
