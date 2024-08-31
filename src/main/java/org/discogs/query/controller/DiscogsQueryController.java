package org.discogs.query.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.discogs.query.service.DiscogsQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * Controller for handling Discogs query-related operations.
 * This controller provides an API endpoint for searching Discogs based on user queries.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
@RequestMapping("discogs-query")
public class DiscogsQueryController {

    private final DiscogsQueryService discogsQueryService;

    /**
     * Searches Discogs using the provided query data.
     *
     * @param discogsQueryDTO the data transfer object containing the search query details
     * @return a {@link ResponseEntity} containing the search results wrapped in {@link DiscogsResult}
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiscogsResultDTO>> searchBasedOnQuery(
            @RequestBody final List<DiscogsQueryDTO> discogsQueryDTO) {

        List<DiscogsResultDTO> resultDTOList = discogsQueryDTO.parallelStream()
                .map(discogsQueryService::searchBasedOnQuery)
                .filter(Objects::nonNull)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(resultDTOList);
    }

}