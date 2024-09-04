package org.discogs.query.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

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
public class DiscogsResultDTO {

    /**
     * The {@link DiscogsQueryDTO} query used to obtain the results
     */
    private DiscogsQueryDTO searchQuery;

    /**
     * A list of {@link DiscogsEntryDTO} objects that represent the search
     * results.
     */
    private List<DiscogsEntryDTO> results;
}