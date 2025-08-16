package org.discogs.query.service.core;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.api.DiscogsEntry;
import org.discogs.query.domain.api.DiscogsResult;
import org.discogs.query.helpers.LogHelper;
import org.discogs.query.interfaces.MappingService;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsMapResultDTO;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.stereotype.Service;

/** Service for mapping Discogs results to the desired DTO format. */
@Slf4j
@Service
@RequiredArgsConstructor
public class MappingServiceImpl implements MappingService {

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
        entry.getNumberForSale());
  }

  /**
   * Converts a {@link DiscogsResultDTO} into a {@link DiscogsMapResultDTO}.
   *
   * @param discogsResultDTO the {@link DiscogsResultDTO} containing the list of entries
   * @return a {@link DiscogsMapResultDTO} with entries grouped by title
   * @throws IllegalArgumentException if {@code discogsResultDTO} is null
   */
  DiscogsMapResultDTO convertEntriesToMapByTitle(final DiscogsResultDTO discogsResultDTO) {
    Objects.requireNonNull(discogsResultDTO, "DiscogsResultDTO must not be null");
    LogHelper.info(() -> "Converting Discogs entries to map by title");

    // Cache the list of entries to avoid multiple iterations.
    var entries = discogsResultDTO.results();
    // Use the teeing collector (Java 12+) to group entries by title and determine the cheapest
    // entry in one pass.
    return entries.stream()
        .collect(
            Collectors.teeing(
                Collectors.groupingBy(DiscogsEntryDTO::title),
                Collectors.minBy(Comparator.comparing(DiscogsEntryDTO::lowestPrice)),
                (groupMap, minOpt) ->
                    new DiscogsMapResultDTO(
                        discogsResultDTO.searchQuery(), groupMap, minOpt.orElse(null))));
  }

  @Override
  public List<DiscogsMapResultDTO> mapResultsToDTO(final List<DiscogsResultDTO> resultDTOList) {
    return resultDTOList.stream().map(this::convertEntriesToMapByTitle).toList();
  }

  @Override
  public DiscogsResultDTO mapObjectToDTO(
      final DiscogsResult discogsResult, final DiscogsQueryDTO discogsQueryDTO) {
    LogHelper.debug(
        () -> "Mapping DiscogsResult to DiscogsResultDTO for query: {}", discogsQueryDTO);
    try {
      var resultDTO =
          new DiscogsResultDTO(discogsQueryDTO, convertEntriesToDTOs(discogsResult.getResults()));
      LogHelper.debug(() -> "Mapping completed for query: {}", discogsQueryDTO);
      return resultDTO;
    } catch (final Exception e) {
      LogHelper.error(
          () -> "Error mapping DiscogsResult to DiscogsResultDTO for query: {}",
          discogsQueryDTO,
          e);
      throw e;
    }
  }

  private List<DiscogsEntryDTO> convertEntriesToDTOs(final List<DiscogsEntry> entries) {
    return entries.stream().map(MappingServiceImpl::convertEntryToEntryDTO).toList();
  }
}
