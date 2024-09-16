package org.discogs.query.model;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing the results of a Discogs search
 * query. This class encapsulates a list of {@link DiscogsEntryDTO} objects that
 * match the search criteria.
 */
public record DiscogsResultDTO(
        /**
         * The {@link DiscogsQueryDTO} query used to obtain the results.
         */
        DiscogsQueryDTO searchQuery,

        /**
         * A list of {@link DiscogsEntryDTO} objects that represent the search
         * results.
         */
        List<DiscogsEntryDTO> results
) {
}
