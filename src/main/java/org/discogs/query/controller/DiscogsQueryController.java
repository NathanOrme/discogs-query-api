package org.discogs.query.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.discogs.query.service.MappingService;
import org.discogs.query.service.QueryProcessingService;
import org.discogs.query.service.ResultCalculationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller for handling Discogs query-related operations.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("discogs-query")
public class DiscogsQueryController {

    private static final String APPLICATION_JSON_VALUE = MediaType.APPLICATION_JSON_VALUE;

    private final QueryProcessingService queryProcessingService;
    private final MappingService mappingService;
    private final ResultCalculationService resultCalculationService;

    @Value("${queries.timeout:59}")
    private int timeoutInSeconds;

    /**
     * Searches Discogs using the provided query data.
     *
     * @param discogsQueryDTO the data transfer objects containing the search query details
     * @return a {@link ResponseEntity} containing a list of {@link DiscogsMapResultDTO}
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/search", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiscogsMapResultDTO>> search(
            @RequestBody @Valid final List<DiscogsQueryDTO> discogsQueryDTO) {

        log.info("Received search request with {} queries", discogsQueryDTO.size());

        List<DiscogsResultDTO> resultDTOList = queryProcessingService.processQueries(discogsQueryDTO, timeoutInSeconds);

        if (resultDTOList.isEmpty() || hasNoEntries(resultDTOList)) {
            log.warn("No results found for the provided queries");
            return ResponseEntity.noContent().build();
        }

        int size = resultCalculationService.calculateSizeOfResults(resultDTOList);
        log.info("Returning {} results: {}", size, resultDTOList);

        List<DiscogsMapResultDTO> resultMapDTOList = mappingService.mapResultsToDTO(resultDTOList);

        filterDuplicateEntries(resultMapDTOList);

        return ResponseEntity.ok().body(resultMapDTOList);
    }

    /**
     * Filters Discogs results to only include items shipping from the UK.
     *
     * @param discogsResultDTOList the list of {@link DiscogsResultDTO} to filter
     * @return a {@link ResponseEntity} containing the filtered list of {@link DiscogsMapResultDTO}
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/filter-uk", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiscogsMapResultDTO>> filterUkMarketplace(
            @RequestBody @Valid final List<DiscogsResultDTO> discogsResultDTOList) {

        if (discogsResultDTOList.isEmpty() || hasNoEntries(discogsResultDTOList)) {
            log.warn("No results found to filter for UK shipping");
            return ResponseEntity.noContent().build();
        }

        log.info("Filtering results to show items that ship from the UK");
        List<DiscogsResultDTO> filteredResultDTOList = queryProcessingService.filterOutEntriesNotShippingFromUk(discogsResultDTOList);

        int size = resultCalculationService.calculateSizeOfResults(filteredResultDTOList);
        log.info("Returning {} filtered results: {}", size, filteredResultDTOList);

        List<DiscogsMapResultDTO> resultMapDTOList = mappingService.mapResultsToDTO(filteredResultDTOList);

        filterDuplicateEntries(resultMapDTOList);

        return ResponseEntity.ok().body(resultMapDTOList);
    }

    private boolean hasNoEntries(final List<DiscogsResultDTO> resultDTOList) {
        for (final DiscogsResultDTO discogsResultDTO : resultDTOList) {
            if (!discogsResultDTO.results().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void filterDuplicateEntries(final List<DiscogsMapResultDTO> discogsMapResultDTOS) {
        for (final DiscogsMapResultDTO discogsMapResultDTO : discogsMapResultDTOS) {
            for (final Map.Entry<String, List<DiscogsEntryDTO>> resultDTO : discogsMapResultDTO.results().entrySet()) {
                List<DiscogsEntryDTO> entries = removeDuplicateEntries(resultDTO.getValue());
                resultDTO.setValue(entries);
            }
        }
    }

    private List<DiscogsEntryDTO> removeDuplicateEntries(final List<DiscogsEntryDTO> values) {
        Set<Integer> seenIds = new HashSet<>();
        return values.stream()
                .filter(entry -> seenIds.add(entry.id()))
                .toList();
    }
}