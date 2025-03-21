package org.discogs.query.interfaces;

import org.discogs.query.domain.api.DiscogsEntry;
import org.discogs.query.domain.api.DiscogsRelease;
import org.discogs.query.domain.api.DiscogsResult;
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

    /**
     * Filters out entries with null or zero lowest price from the given {@link DiscogsResult}.
     *
     * <p>This method updates the results in the given {@link DiscogsResult} object by removing any
     * entries where the lowest price is either null or zero. If the results list is null, it sets the
     * results to an empty list.
     *
     * @param results the {@link DiscogsResult} object containing a list of entries to be filtered
     */
    void filterOutEmptyLowestPrice(DiscogsResult results);
}
