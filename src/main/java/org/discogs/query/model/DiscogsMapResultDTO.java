package org.discogs.query.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object (DTO) representing the results of a Discogs search
 * query.
 * This class encapsulates a list of {@link DiscogsEntryDTO} objects that
 * match the search criteria.
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DiscogsMapResultDTO {

    /**
     * The {@link DiscogsQueryDTO} query used to obtain the results
     */
    private DiscogsQueryDTO searchQuery;

    /**
     * A Map of {@link DiscogsEntryDTO} objects that represent the search
     * results.
     * The key is the title of the release
     */
    private Map<String, List<DiscogsEntryDTO>> results;
}