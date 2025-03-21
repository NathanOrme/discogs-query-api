package org.discogs.query.service;

import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for calculating the size of results from Discogs queries.
 */
@Service
public class ResultCalculationService {

    /**
     * Calculates the total number of results across all {@link DiscogsResultDTO} objects.
     *
     * @param resultDTOList the list of {@link DiscogsResultDTO} objects
     * @return the total number of results
     */
    public int calculateSizeOfResults(final List<DiscogsResultDTO> resultDTOList) {
        return resultDTOList.parallelStream()
                .filter(discogsResultDTO -> discogsResultDTO.results() != null)
                .mapToInt(discogsResultDTO -> discogsResultDTO.results().size())
                .sum();
    }
}
