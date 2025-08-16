package org.discogs.query.interfaces;

import java.util.List;
import org.discogs.query.domain.api.DiscogsResult;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;

/** Interface for mapping Discogs results to the desired DTO format. */
public interface MappingService {

  /**
   * Maps a list of {@link DiscogsResultDTO} to a list of {@link DiscogsMapResultDTO}.
   *
   * @param resultDTOList the list of {@link DiscogsResultDTO}
   * @return a list of {@link DiscogsMapResultDTO}
   */
  List<DiscogsMapResultDTO> mapResultsToDTO(List<DiscogsResultDTO> resultDTOList);

  /**
   * Maps a {@link DiscogsResult} to a {@link DiscogsResultDTO}.
   *
   * @param discogsResult the {@link DiscogsResult} object
   * @param discogsQueryDTO the {@link DiscogsQueryDTO} used for the query
   * @return a {@link DiscogsResultDTO} corresponding to the {@link DiscogsResult}
   */
  DiscogsResultDTO mapObjectToDTO(DiscogsResult discogsResult, DiscogsQueryDTO discogsQueryDTO);
}