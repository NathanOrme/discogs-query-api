package org.discogs.query.service.external;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.api.DiscogsCollectionRelease;
import org.discogs.query.helpers.LogHelper;
import org.discogs.query.interfaces.DiscogsAPIClient;
import org.discogs.query.interfaces.DiscogsCollectionService;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.discogs.query.util.DiscogsUrlBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscogsCollectionServiceImpl implements DiscogsCollectionService {

  private final DiscogsUrlBuilder discogsUrlBuilder;
  private final DiscogsAPIClient discogsAPIClient;

  @Value("${queries.searchCollection}")
  private boolean searchCollection;

  @Override
  public List<DiscogsResultDTO> filterOwnedReleases(
      final String username, final List<DiscogsResultDTO> entries) {
    if (!searchCollection || entries == null || entries.isEmpty()) {
      if (entries == null || entries.isEmpty()) {
        LogHelper.warn(() -> "No entries provided for filtering.");
      }
      return entries;
    }
    return entries.stream()
        .map(
            entry -> {
              List<DiscogsEntryDTO> filtered =
                  Optional.ofNullable(entry.results()).orElse(List.of()).stream()
                      .filter(release -> !isReleaseOwnedByUser(username, release.id()))
                      .toList();
              if (filtered.isEmpty()) {
                LogHelper.warn(
                    () -> "Skipping entry {}: no remaining results", entry.searchQuery());
              } else {
                LogHelper.info(() -> "Filtered results for {}: {}", entry.searchQuery(), filtered);
              }
              return new DiscogsResultDTO(entry.searchQuery(), filtered);
            })
        .toList();
  }

  /**
   * Checks if a specific release is owned by the given user.
   *
   * @param username Discogs username.
   * @param releaseId ID of the release to check.
   * @return true if the release is owned by the user, false otherwise.
   */
  private boolean isReleaseOwnedByUser(final String username, final int releaseId) {
    try {
      String collectionUrl =
          discogsUrlBuilder.buildCollectionSearchUrl(username, String.valueOf(releaseId));
      DiscogsCollectionRelease ownedRelease = discogsAPIClient.getCollectionReleases(collectionUrl);
      var releases = Optional.ofNullable(ownedRelease.releases()).orElse(List.of());
      boolean owned =
          releases.stream()
              .map(DiscogsCollectionRelease.Release::id)
              .anyMatch(foundId -> foundId != null && foundId == releaseId);
      if (owned) {
        LogHelper.info(() -> "Release ID {} is owned by user {}", releaseId, username);
        return true;
      }
    } catch (final Exception e) {
      LogHelper.error(
          () -> "Error checking ownership for release ID {} and username {}: {}",
          releaseId,
          username,
          e.getMessage(),
          e);
    }
    return false;
  }
}
