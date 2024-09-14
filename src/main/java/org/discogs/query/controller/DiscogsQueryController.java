package org.discogs.query.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.discogs.query.service.QueryProcessingService;
import org.discogs.query.service.ResultCalculationService;
import org.discogs.query.service.ResultMappingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @Value("${queries.timeout:59}")
    private int timeoutInSeconds;

    private final QueryProcessingService queryProcessingService;
    private final ResultMappingService resultMappingService;
    private final ResultCalculationService resultCalculationService;

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

        List<DiscogsResultDTO> resultDTOList = queryProcessingService.processQueries(discogsQueryDTO, timeoutInSeconds);

        if (resultDTOList.isEmpty()) {
            log.warn("No results found for the provided queries");
            return ResponseEntity.noContent().build();
        }

        int size = resultCalculationService.calculateSizeOfResults(resultDTOList);
        log.info("Returning {} results: {}", size, resultDTOList);

        List<DiscogsMapResultDTO> resultMapDTOList = resultMappingService.mapResultsToDTO(resultDTOList);

        return ResponseEntity.ok().body(resultMapDTOList);
    }
}
