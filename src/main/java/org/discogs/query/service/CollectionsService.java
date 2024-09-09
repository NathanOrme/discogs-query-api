package org.discogs.query.service;

import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class responsible for operations related to collections and data transformation.
 */
@Service
public class CollectionsService {

    /**
     * Converts a list of Discogs entries from a {@link DiscogsResultDTO} object into a map and stores the map in a
     * {@link DiscogsMapResultDTO} object. The map uses the entry titles as keys and the corresponding
     * {@link DiscogsEntryDTO} objects as values.
     *
     * @param entry the {@link DiscogsResultDTO} containing the list of Discogs entries to be converted
     * @return a {@link DiscogsMapResultDTO} containing the search query and the converted map of entries
     */
    public DiscogsMapResultDTO convertListToMapForDTO(final DiscogsResultDTO entry) {
        DiscogsMapResultDTO discogsMapResultDTO = new DiscogsMapResultDTO();
        discogsMapResultDTO.setSearchQuery(entry.getSearchQuery());

        Map<String, DiscogsEntryDTO> mapOfEntries = entry.getResults().stream()
                .collect(Collectors.toMap(DiscogsEntryDTO::getTitle, discogsEntryDTO -> discogsEntryDTO));

        discogsMapResultDTO.setResults(mapOfEntries);
        return discogsMapResultDTO;
    }
}
