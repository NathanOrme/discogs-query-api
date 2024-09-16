package org.discogs.query.model;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object (DTO) representing the results of a Discogs search
 * query. This class encapsulates a map of {@link DiscogsEntryDTO} objects that
 * match the search criteria.
 */
public record DiscogsMapResultDTO(
        /**
         * The {@link DiscogsQueryDTO} query used to obtain the results.
         */
        DiscogsQueryDTO searchQuery,

        /**
         * A Map of {@link DiscogsEntryDTO} objects that represent the search
         * results. The key is the title of the release.
         */
        Map<String, List<DiscogsEntryDTO>> results
) {
}
