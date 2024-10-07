package org.discogs.query.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.helpers.LogHelper;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private static final String APPLICATION_JSON_VALUE = MediaType.APPLICATION_JSON_VALUE;

    private final QueryProcessingService queryProcessingService;
    private final MappingService mappingService;
    private final ResultCalculationService resultCalculationService;

    @Value("${queries.timeout:59}")
    private int timeoutInSeconds;

    @Value("${queries.filterForUk:true}")
    private boolean isFilterForUk;

    /**
     * Searches Discogs using the provided query data.
     *
     * @param discogsQueryDTO the data transfer objects containing the search
     *                        query details
     * @return a {@link ResponseEntity} containing a list of {@link DiscogsMapResultDTO}
     * wrapped in {@link HttpStatus#OK} if results are found, or an empty list if no results are found
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/search", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DiscogsMapResultDTO>> search(
            @RequestBody @Valid final List<DiscogsQueryDTO> discogsQueryDTO) {

        log.info("Received search request with {} queries", discogsQueryDTO.size());
        List<DiscogsResultDTO> resultDTOList = queryProcessingService.processQueries(discogsQueryDTO, timeoutInSeconds);

        if (resultDTOList.isEmpty() || hasNoEntries(resultDTOList)) {
            LogHelper.warn(log, () -> "No results found for the provided queries");
            return ResponseEntity.noContent().build();
        }
        if (isFilterForUk) {
            LogHelper.info(log, () -> "Filtering results to show items that ship from the UK");
            resultDTOList = queryProcessingService.filterOutEntriesNotShippingFromUk(resultDTOList);
        }

        int size = resultCalculationService.calculateSizeOfResults(resultDTOList);
        LogHelper.info(log, () -> "Returning {} results: {}", size, resultDTOList);

        List<DiscogsMapResultDTO> resultMapDTOList = mappingService.mapResultsToDTO(resultDTOList);

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


    /**
     * Filters duplicate entries from a list of DiscogsMapResultDTO.
     *
     * @param discogsMapResultDTOS the list of DiscogsMapResultDTO to filter
     */
    private void filterDuplicateEntries(final List<DiscogsMapResultDTO> discogsMapResultDTOS) {
        for (final DiscogsMapResultDTO discogsMapResultDTO : discogsMapResultDTOS) {
            for (final Map.Entry<String, List<DiscogsEntryDTO>> resultDTO : discogsMapResultDTO.results().entrySet()) {
                List<DiscogsEntryDTO> entries = removeDuplicateEntries(resultDTO.getValue());
                resultDTO.setValue(entries);
            }
        }
    }

    /**
     * Removes duplicate entries from a list of DiscogsEntryDTO based on their IDs.
     *
     * @param values the list of DiscogsEntryDTO to filter
     * @return a list of unique DiscogsEntryDTO without duplicates
     */
    private List<DiscogsEntryDTO> removeDuplicateEntries(final List<DiscogsEntryDTO> values) {
        Set<Integer> seenIds = new HashSet<>();
        return values.stream()
                .filter(entry -> seenIds.add(entry.id())) // add returns false if the id was already present
                .toList();
    }

}
