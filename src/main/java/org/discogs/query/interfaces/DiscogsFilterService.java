package org.discogs.query.interfaces;

import org.discogs.query.domain.DiscogsEntry;
import org.discogs.query.domain.DiscogsRelease;
import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.model.DiscogsQueryDTO;

/**
 * Service interface for filtering and sorting Discogs search results.
 */
public interface DiscogsFilterService {

    /**
     * Filters and sorts the search results based on the query DTO.
     *
     * @param discogsQueryDTO the search query data transfer object
     * @param results         the search results to be filtered and sorted
     */
    void filterAndSortResults(DiscogsQueryDTO discogsQueryDTO, DiscogsResult results);

    /**
     * Retrieves a {@link DiscogsRelease} object based on the provided {@link DiscogsEntry}.
     *
     * @param discogsEntry the Discogs entry containing the release ID
     * @return the {@link DiscogsRelease} object
     */
    DiscogsRelease getReleaseDetails(DiscogsEntry discogsEntry);
}
