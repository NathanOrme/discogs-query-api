package org.discogs.query.model;

import java.util.List;

/**
 * Request DTO for emailing results. Extends the standard request with an email address that will
 * receive the results.
 */
public record EmailSearchRequestDTO(List<DiscogsQueryDTO> queries, String username, String email) {}
