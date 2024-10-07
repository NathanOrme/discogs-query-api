package org.discogs.query.interfaces;

import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsResultDTO;

/**
 * Interface defining operations related to collections and data transformation.
 */
public interface DiscogsMappingService {

    /**
     * Converts a list of Discogs entries from a {@link DiscogsResultDTO} object into a map and stores the map in a
     * {@link DiscogsMapResultDTO} object. The map uses the entry titles as keys and lists of corresponding
     * {@link DiscogsEntryDTO} objects as values.
     *
     * @param discogsResultDTO the {@link DiscogsResultDTO} containing the list of Discogs entries to be converted
     * @return a {@link DiscogsMapResultDTO} containing the search query and the converted map of entries
     */
    DiscogsMapResultDTO convertEntriesToMapByTitle(DiscogsResultDTO discogsResultDTO);
}

