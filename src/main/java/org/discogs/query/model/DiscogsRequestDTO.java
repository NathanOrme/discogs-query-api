package org.discogs.query.model;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a request for the API.
 */
public record DiscogsRequestDTO(
        List<DiscogsQueryDTO> queries,
        String username
) {
}
