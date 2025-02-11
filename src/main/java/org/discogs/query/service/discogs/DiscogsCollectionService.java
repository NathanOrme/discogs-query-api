package org.discogs.query.service.discogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.api.DiscogsCollectionRelease;
import org.discogs.query.helpers.DiscogsUrlBuilder;
import org.discogs.query.helpers.LogHelper;
import org.discogs.query.interfaces.DiscogsAPIClient;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscogsCollectionService {

  @Value("${queries.searchCollection}")
  private boolean searchCollection;

  private final DiscogsUrlBuilder discogsUrlBuilder;
  private final DiscogsAPIClient discogsAPIClient;

  /**
   * Filters out all results already owned by the username in their Discogs collection.
   *
   * @param username Username to search against.
   * @param entries List of Discogs search results to filter, using their IDs.
   */
  public List<DiscogsResultDTO> filterOwnedReleases(
      final String username, final List<DiscogsResultDTO> entries) {
    if (!searchCollection) {
      return entries;
    }
    if (entries == null || entries.isEmpty()) {
      LogHelper.warn(() -> "No entries provided for filtering.");
      return entries;
    }

    // Create a mutable copy of the list if it's immutable
    List<DiscogsResultDTO> mutableEntries = new ArrayList<>(entries);

    for (int i = 0; i < mutableEntries.size(); i++) {
      final DiscogsResultDTO entry = mutableEntries.get(i);
      if (entry.results() == null || entry.results().isEmpty()) {
        LogHelper.warn(() -> "Skipping empty results for entry: {}", entry.searchQuery());
        continue;
      }
      List<DiscogsEntryDTO> filteredResults =
          entry.results().stream()
              .filter(release -> !isReleaseOwnedByUser(username, release.id()))
              .toList();
      LogHelper.info(() -> "Updating entry with filtered results: {}", filteredResults);
      mutableEntries.set(i, new DiscogsResultDTO(entry.searchQuery(), filteredResults));
      LogHelper.info(() -> "Filtered results for Result: {}", filteredResults);
    }

    return mutableEntries;
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
