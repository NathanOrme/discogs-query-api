package org.discogs.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.api.DiscogsEntry;
import org.discogs.query.domain.api.DiscogsResult;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service for mapping Discogs results to the desired DTO format.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MappingService {

    private static DiscogsEntryDTO convertEntryToEntryDTO(final DiscogsEntry entry) {
        return new DiscogsEntryDTO(
                entry.getId(),
                entry.getTitle(),
                entry.getFormat(),
                entry.getUrl(),
                entry.getUri(),
                entry.getCountry(),
                entry.getYear(),
                entry.getIsOnMarketplace(),
                entry.getLowestPrice(),
                entry.getNumberForSale()
        );
    }

    /**
     * Converts a {@link DiscogsResultDTO} into a {@link DiscogsMapResultDTO}.
     *
     * @param discogsResultDTO the {@link DiscogsResultDTO} containing the list of entries
     * @return a {@link DiscogsMapResultDTO} with entries grouped by title
     * @throws IllegalArgumentException if {@code discogsResultDTO} is null
     */
    DiscogsMapResultDTO convertEntriesToMapByTitle(final DiscogsResultDTO discogsResultDTO) {
        Objects.requireNonNull(discogsResultDTO, "DiscogsResultDTO must not be null");

        log.info("Converting Discogs entries to map by title");

        DiscogsEntryDTO cheapestItem = discogsResultDTO.results().stream()
                .min(Comparator.comparing(DiscogsEntryDTO::lowestPrice))
                .orElse(null);

        Map<String, List<DiscogsEntryDTO>> mapOfEntries = discogsResultDTO.results()
                .stream()
                .collect(Collectors.groupingBy(DiscogsEntryDTO::title));

        return new DiscogsMapResultDTO(discogsResultDTO.searchQuery(), mapOfEntries, cheapestItem);
    }

    /**
     * Maps a list of {@link DiscogsResultDTO} to a list of {@link DiscogsMapResultDTO}.
     *
     * @param resultDTOList the list of {@link DiscogsResultDTO}
     * @return a list of {@link DiscogsMapResultDTO}
     */
    public List<DiscogsMapResultDTO> mapResultsToDTO(final List<DiscogsResultDTO> resultDTOList) {
        return resultDTOList.parallelStream()
                .map(this::convertEntriesToMapByTitle)
                .toList();
    }

    /**
     * Maps a {@link DiscogsResult} to a {@link DiscogsResultDTO}.
     *
     * @param discogsResult   the {@link DiscogsResult} object
     * @param discogsQueryDTO the {@link DiscogsQueryDTO} used for the query
     * @return a {@link DiscogsResultDTO} corresponding to the {@link DiscogsResult}
     */
    public DiscogsResultDTO mapObjectToDTO(final DiscogsResult discogsResult, final DiscogsQueryDTO discogsQueryDTO) {
        log.debug("Mapping DiscogsResult to DiscogsResultDTO for query: {}", discogsQueryDTO);

        try {
            var resultDTO = new DiscogsResultDTO(discogsQueryDTO, convertEntriesToDTOs(discogsResult.getResults()));
            log.debug("Mapping completed for query: {}", discogsQueryDTO);
            return resultDTO;
        } catch (final Exception e) {
            log.error("Error mapping DiscogsResult to DiscogsResultDTO for query: {}", discogsQueryDTO, e);
            throw e;
        }
    }

    private List<DiscogsEntryDTO> convertEntriesToDTOs(final List<DiscogsEntry> entries) {
        return entries.parallelStream()
                .map(MappingService::convertEntryToEntryDTO)
                .toList();
    }
}
