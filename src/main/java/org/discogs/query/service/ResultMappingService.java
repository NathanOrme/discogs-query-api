package org.discogs.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.interfaces.DiscogsMappingService;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.modelmapper.ModelMapper;
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
    public DiscogsResultDTO mapObjectToDTO(final DiscogsResult discogsResult,
                                           final DiscogsQueryDTO discogsQueryDTO) {
        log.debug("Starting mapping of DiscogsResult to DiscogsResultDTO for " +
                "query: {}", discogsQueryDTO);

        try {
            ModelMapper modelMapper = new ModelMapper();
            var resultDTO = modelMapper.map(discogsResult,
                    DiscogsResultDTO.class);
            resultDTO.setSearchQuery(discogsQueryDTO);

            log.debug("Mapping completed successfully for query: {}",
                    discogsQueryDTO);
            return resultDTO;
        } catch (final Exception e) {
            log.error("Error occurred while mapping DiscogsResult to " +
                            "DiscogsResultDTO for query: {}",
                    discogsQueryDTO, e);
            throw e;  // Re-throw the exception after logging
        }
    }
}
