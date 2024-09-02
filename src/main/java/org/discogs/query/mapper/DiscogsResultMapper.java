package org.discogs.query.mapper;

import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * A component responsible for mapping {@link DiscogsResult} objects to {@link DiscogsResultDTO} objects.
 * <p>
 * This class uses {@link ModelMapper} to facilitate the transformation of
 * domain objects into their respective Data Transfer Objects (DTOs).
 */
@Component
public class DiscogsResultMapper {

    private static final Logger log = LoggerFactory.getLogger(DiscogsResultMapper.class);

    /**
     * Maps a {@link DiscogsResult} object to a {@link DiscogsResultDTO} object.
     *
     * @param discogsResult   the {@link DiscogsResult} object to be mapped
     * @param discogsQueryDTO {@link DiscogsQueryDTO} query used to get the results
     * @return a {@link DiscogsResultDTO} object that corresponds to the provided {@link DiscogsResult}
     */
    public DiscogsResultDTO mapObjectToDTO(final DiscogsResult discogsResult, final DiscogsQueryDTO discogsQueryDTO) {
        log.debug("Starting mapping of DiscogsResult to DiscogsResultDTO for query: {}", discogsQueryDTO);

        try {
            ModelMapper modelMapper = new ModelMapper();
            var resultDTO = modelMapper.map(discogsResult, DiscogsResultDTO.class);
            resultDTO.setSearchQuery(discogsQueryDTO);

            log.debug("Mapping completed successfully for query: {}", discogsQueryDTO);
            return resultDTO;
        } catch (final Exception e) {
            log.error("Error occurred while mapping DiscogsResult to DiscogsResultDTO for query: {}",
                    discogsQueryDTO, e);
            throw e;  // Re-throw the exception after logging
        }
    }
}
