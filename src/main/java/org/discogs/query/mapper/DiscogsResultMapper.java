package org.discogs.query.mapper;

import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.model.DiscogsResultDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * A component responsible for mapping {@link DiscogsResult} objects to {@link DiscogsResultDTO} objects.
 * <p>
 * This class uses {@link ModelMapper} to facilitate the transformation of domain objects into their respective Data Transfer Objects (DTOs).
 * </p>
 */
@Component
public class DiscogsResultMapper {

    /**
     * Maps a {@link DiscogsResult} object to a {@link DiscogsResultDTO} object.
     *
     * @param discogsResult the {@link DiscogsResult} object to be mapped
     * @return a {@link DiscogsResultDTO} object that corresponds to the provided {@link DiscogsResult}
     */
    public DiscogsResultDTO mapObjectToDTO(final DiscogsResult discogsResult) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(discogsResult, DiscogsResultDTO.class);
    }

}