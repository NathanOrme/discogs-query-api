package org.discogs.query.mapper;

import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.domain.db.DiscogsResultDb;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

/**
 * A component responsible for mapping {@link DiscogsResult} objects to
 * {@link DiscogsResultDTO} objects.
 * <p>
 * This class uses {@link ModelMapper} to facilitate the transformation of
 * domain objects into their respective Data Transfer Objects (DTOs).
 */
@Slf4j
@Component
public class DiscogsResultMapper {

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

    /**
     * Maps a {@link DiscogsResult} object to a {@link DiscogsResultDb} object.
     *
     * @param discogsResult the {@link DiscogsResult} object to be mapped
     * @param searchUrl     Url used for the query
     * @return a {@link DiscogsResultDb} object that corresponds to the
     * provided {@link DiscogsResult}
     */
    public DiscogsResultDb mapObjectToDbEntity(final DiscogsResult discogsResult,
                                               final String searchUrl) {
        log.debug("Starting mapping of DiscogsResult to DiscogsResultDb for result: {}", discogsResult);

        try {
            ModelMapper modelMapper = new ModelMapper();

            // Ensure that ModelMapper is configured to handle nested mappings correctly
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

            // Map the DiscogsResult to DiscogsResultDb
            var resultDb = modelMapper.map(discogsResult, DiscogsResultDb.class);
            resultDb.setSearchUrl(searchUrl);

            // Ensure that the relationships are correctly set
            if (resultDb.getResults() != null) {
                resultDb.getResults().forEach(discogsEntryDb -> discogsEntryDb.setDiscogsResultDb(resultDb));
            }

            log.debug("Mapping completed successfully for result: {}", discogsResult);
            return resultDb;
        } catch (final Exception e) {
            log.error("Error occurred while mapping DiscogsResult to DiscogsResultDb for result: {}", discogsResult, e);
            throw e;  // Re-throw the exception after logging
        }
    }

    /**
     * Maps a {@link DiscogsResult} object to a {@link DiscogsResultDTO} object.
     *
     * @param discogsResultDb the {@link DiscogsResultDb} object to be mapped
     * @return a {@link DiscogsResult} object that corresponds to the
     * provided {@link DiscogsResultDb}
     */
    public DiscogsResult mapDbEntityToObject(final DiscogsResultDb discogsResultDb) {
        log.debug("Starting mapping of DiscogsResultDb to DiscogsResult for " +
                "result: {}", discogsResultDb);

        try {
            ModelMapper modelMapper = new ModelMapper();
            var resultDb = modelMapper.map(discogsResultDb, DiscogsResult.class);

            log.debug("Mapping completed successfully for result: {}",
                    discogsResultDb);
            return resultDb;
        } catch (final Exception e) {
            log.error("Error occurred while mapping DiscogsResultDb to DiscogsResult for result: {}",
                    discogsResultDb, e);
            throw e;  // Re-throw the exception after logging
        }
    }

}
