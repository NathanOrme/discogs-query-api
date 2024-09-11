package org.discogs.query.service;

import org.discogs.query.interfaces.CollectionsService;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class responsible for operations related to collections and data transformation.
 */
@Service
public class CollectionsServiceImpl implements CollectionsService {

    /**
     * Converts a list of Discogs entries from a {@link DiscogsResultDTO} object into a map and stores the map in a
     * {@link DiscogsMapResultDTO} object. The map uses the entry titles as keys and lists of corresponding
     * {@link DiscogsEntryDTO} objects as values.
     *
     * @param discogsResultDTO the {@link DiscogsResultDTO} containing the list of Discogs entries to be converted
     * @return a {@link DiscogsMapResultDTO} containing the search query and the converted map of entries
     */
    @Override
    public DiscogsMapResultDTO convertListToMapForDTO(final DiscogsResultDTO discogsResultDTO) {
        DiscogsMapResultDTO discogsMapResultDTO = new DiscogsMapResultDTO();
        discogsMapResultDTO.setSearchQuery(discogsResultDTO.getSearchQuery());

        Map<String, List<DiscogsEntryDTO>> mapOfEntries = new HashMap<>();

        for (final DiscogsEntryDTO entryDTO : discogsResultDTO.getResults()) {
            mapOfEntries
                    .computeIfAbsent(entryDTO.getTitle(), k -> new ArrayList<>())
                    .add(entryDTO);
        }

        discogsMapResultDTO.setResults(mapOfEntries);
        return discogsMapResultDTO;
    }
}
