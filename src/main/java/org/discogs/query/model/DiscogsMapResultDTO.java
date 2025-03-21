package org.discogs.query.model;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object (DTO) representing the results of a Discogs search query. This class
 * encapsulates a map of {@link DiscogsEntryDTO} objects that match the search criteria.
 */
public record DiscogsMapResultDTO(
        DiscogsQueryDTO searchQuery,
        Map<String, List<DiscogsEntryDTO>> results,
        DiscogsEntryDTO cheapestItem) {
}
