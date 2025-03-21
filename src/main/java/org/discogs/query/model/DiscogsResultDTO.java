package org.discogs.query.model;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing the results of a Discogs search query. This class
 * encapsulates a list of {@link DiscogsEntryDTO} objects that match the search criteria.
 */
public record DiscogsResultDTO(DiscogsQueryDTO searchQuery, List<DiscogsEntryDTO> results) {
}
