package org.discogs.query.service;

import lombok.extern.slf4j.Slf4j;
import org.discogs.query.interfaces.DiscogsMappingService;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service class responsible for operations related to collections and data transformation.
 */
@Slf4j
@Service
public class DiscogsMappingServiceImpl implements DiscogsMappingService {

    /**
     * Converts a list of Discogs entries from a {@link DiscogsResultDTO} object into a map where the keys are
     * entry titles and the values are lists of corresponding {@link DiscogsEntryDTO} objects. The result is stored in
     * a {@link DiscogsMapResultDTO} object.
     *
     * @param discogsResultDTO the {@link DiscogsResultDTO} containing the list of Discogs entries to be converted
     * @return a {@link DiscogsMapResultDTO} containing the search query and the converted map of entries
     * @throws IllegalArgumentException if {@code discogsResultDTO} is null
     */
    @Override
    public DiscogsMapResultDTO convertEntriesToMapByTitle(final DiscogsResultDTO discogsResultDTO) {
        Objects.requireNonNull(discogsResultDTO, "DiscogsResultDTO must not be null");

        log.info("Converting Discogs entries to map by title");

        DiscogsMapResultDTO discogsMapResultDTO = new DiscogsMapResultDTO();
        discogsMapResultDTO.setSearchQuery(discogsResultDTO.getSearchQuery());

        Map<String, List<DiscogsEntryDTO>> mapOfEntries = discogsResultDTO.getResults()
                .stream()
                .collect(Collectors.groupingBy(DiscogsEntryDTO::getTitle));

        discogsMapResultDTO.setResults(mapOfEntries);
        return discogsMapResultDTO;
    }
}
