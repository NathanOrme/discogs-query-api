package org.discogs.query.service;

import lombok.RequiredArgsConstructor;
import org.discogs.query.interfaces.DiscogsMappingService;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for mapping Discogs results to the desired DTO format.
 */
@Service
@RequiredArgsConstructor
public class ResultMappingService {

    private final DiscogsMappingService discogsMappingService;

    /**
     * Maps the {@link DiscogsResultDTO} objects to {@link DiscogsMapResultDTO} objects.
     *
     * @param resultDTOList the list of {@link DiscogsResultDTO} objects
     * @return a list of {@link DiscogsMapResultDTO} objects
     */
    public List<DiscogsMapResultDTO> mapResultsToDTO(final List<DiscogsResultDTO> resultDTOList) {
        return resultDTOList.parallelStream()
                .map(discogsMappingService::convertEntriesToMapByTitle)
                .toList();
    }
}
