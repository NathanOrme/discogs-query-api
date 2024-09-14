package org.discogs.query.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.interfaces.DiscogsMappingService;
import org.discogs.query.interfaces.DiscogsQueryService;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
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
 * This controller provides an API endpoint for searching Discogs based on
 * user queries.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("discogs-query")
public class DiscogsQueryController {

    private final DiscogsQueryService discogsQueryService;
    private final DiscogsMappingService discogsMappingService;

    /**
     * Searches Discogs using the provided query data.
     *
     * @param discogsQueryDTO the data transfer objects containing the search
     *                        query details
     * @return a {@link ResponseEntity} containing a list of {@link DiscogsMapResultDTO}
     * wrapped in {@link HttpStatus#OK} if results are found, or an empty list if no results are found
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiscogsMapResultDTO>> search(
            @RequestBody @Valid final List<DiscogsQueryDTO> discogsQueryDTO) {

        log.info("Received search request with {} queries", discogsQueryDTO.size());

        List<DiscogsResultDTO> resultDTOList = processQueries(discogsQueryDTO);

        if (resultDTOList.isEmpty()) {
            log.warn("No results found for the provided queries");
            return ResponseEntity.noContent().build();
        }

        int size = calculateSizeOfResults(resultDTOList);
        log.info("Returning {} results: {}", size, resultDTOList);

        List<DiscogsMapResultDTO> resultMapDTOList = mapResultsToDTO(resultDTOList);

        return ResponseEntity.ok().body(resultMapDTOList);
    }

    /**
     * Processes each query and retrieves the results from the Discogs API.
     *
     * @param discogsQueryDTOList the list of {@link DiscogsQueryDTO} objects to process
     * @return a list of {@link DiscogsResultDTO} objects
     */
    private List<DiscogsResultDTO> processQueries(final List<DiscogsQueryDTO> discogsQueryDTOList) {
        return discogsQueryDTOList.stream()
                .map(query -> {
                    log.debug("Processing query: {}", query);
                    return discogsQueryService.searchBasedOnQuery(query);
                })
                .filter(Objects::nonNull)
                .peek(result -> log.debug("Received result: {}", result))
                .toList();
    }

    /**
     * Maps the {@link DiscogsResultDTO} objects to {@link DiscogsMapResultDTO} objects
     * using the {@link DiscogsMappingService}.
     *
     * @param resultDTOList the list of {@link DiscogsResultDTO} objects
     * @return a list of {@link DiscogsMapResultDTO} objects
     */
    private List<DiscogsMapResultDTO> mapResultsToDTO(final List<DiscogsResultDTO> resultDTOList) {
        return resultDTOList.parallelStream()
                .map(discogsMappingService::convertEntriesToMapByTitle)
                .toList();
    }

    /**
     * Calculates the total number of results across all {@link DiscogsResultDTO} objects.
     *
     * @param resultDTOList the list of {@link DiscogsResultDTO} objects
     * @return the total number of results
     */
    private int calculateSizeOfResults(final List<DiscogsResultDTO> resultDTOList) {
        return resultDTOList.parallelStream()
                .filter(discogsResultDTO -> discogsResultDTO.getResults() != null)
                .mapToInt(discogsResultDTO -> discogsResultDTO.getResults().size())
                .sum();
    }

}
