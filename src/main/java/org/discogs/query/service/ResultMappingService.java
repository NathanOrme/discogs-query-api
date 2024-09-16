package org.discogs.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.DiscogsEntry;
import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.interfaces.DiscogsMappingService;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for mapping Discogs results to the desired DTO format.
 */
@Slf4j
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

    /**
     * Maps a {@link DiscogsResult} object to a {@link DiscogsResultDTO} object.
     *
     * @param discogsResult   the {@link DiscogsResult} object to be mapped
     * @param discogsQueryDTO {@link DiscogsQueryDTO} query used to get the
     *                        results
     * @return a {@link DiscogsResultDTO} object that corresponds to the
     * provided {@link DiscogsResult}
     */
    public DiscogsResultDTO mapObjectToDTO(final DiscogsResult discogsResult, final DiscogsQueryDTO discogsQueryDTO) {
        log.debug("Starting mapping of DiscogsResult to DiscogsResultDTO for query: {}", discogsQueryDTO);

        try {
            var resultDTO = mapToResultDTO(discogsResult);
            resultDTO = new DiscogsResultDTO(discogsQueryDTO, resultDTO.results());

            log.debug("Mapping completed successfully for query: {}", discogsQueryDTO);
            return resultDTO;
        } catch (final Exception e) {
            log.error("Error occurred while mapping DiscogsResult to DiscogsResultDTO for query: {}",
                    discogsQueryDTO, e);
            throw e;
        }
    }

    private DiscogsResultDTO mapToResultDTO(final DiscogsResult discogsResult) {
        return new DiscogsResultDTO(null, convertEntriesToDTOs(discogsResult.getResults()));
    }

    private List<DiscogsEntryDTO> convertEntriesToDTOs(final List<DiscogsEntry> entries) {
        return entries.parallelStream()
                .map(ResultMappingService::convertEntryToEntryDTO)
                .toList();
    }

    private static DiscogsEntryDTO convertEntryToEntryDTO(final DiscogsEntry entry) {
        return new DiscogsEntryDTO(
                entry.getId(),
                entry.getTitle(),
                entry.getFormat(),
                entry.getUrl(),
                entry.getUri(),
                entry.getCountry(),
                entry.getYear(),
                entry.getIsOnMarketplace(),
                entry.getLowestPrice(),
                entry.getNumberForSale()
        );
    }
}
