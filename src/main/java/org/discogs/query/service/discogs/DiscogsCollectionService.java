package org.discogs.query.service.discogs;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscogsCollectionService {

    @Value("${queries.searchCollection}")
    private boolean searchCollection;

    private final DiscogsUrlBuilder discogsUrlBuilder;
    private final DiscogsAPIClient discogsAPIClient;

    /**
     * Method that filters out all results already owned by the username in their Discogs collection.
     *
     * @param username Username to search against.
     * @param entries  List of Discogs search results to filter, using their IDs.
     */
    public List<DiscogsResultDTO> filterOwnedReleases(final String username, final List<DiscogsResultDTO> entries) {
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

            // Filter out owned releases from the current entry's results
            List<DiscogsEntryDTO> filteredResults = entry.results().stream()
                    .filter(release -> !isReleaseOwnedByUser(username, release.id()))
                    .toList();

            // Log the index and the filtered results for debugging
            LogHelper.info(() -> "Updating entry at index {} with filtered results: {}", i, filteredResults);

            // Update the results in-place with filtered results
            mutableEntries.set(i, new DiscogsResultDTO(entry.searchQuery(), filteredResults));

            LogHelper.info(() -> "Filtered results for Result: {}", filteredResults);
        }

        return mutableEntries;
    }


    /**
     * Checks if a specific release is owned by the given user.
     *
     * @param username  Discogs username.
     * @param releaseId ID of the release to check.
     * @return true if the release is owned by the user, false otherwise.
     */
    private boolean isReleaseOwnedByUser(final String username, final int releaseId) {
        try {
            // Build the URL to check the specific release
            String collectionUrl = discogsUrlBuilder.buildCollectionSearchUrl(username, String.valueOf(releaseId));
            DiscogsCollectionRelease ownedRelease = discogsAPIClient.getCollectionReleases(collectionUrl);
            Optional<Long> ownedReleaseID = Optional.ofNullable(ownedRelease.releases()) // Safely wrap the releases
                    // list in an Optional
                    .stream()
                    .flatMap(List::stream)
                    .map(DiscogsCollectionRelease.Release::id)
                    .filter(foundReleaseId -> isMatchingId(releaseId, foundReleaseId))
                    .findFirst();


            if (ownedReleaseID.isPresent()) {
                LogHelper.info(() -> "Release ID {} is owned by user {}", releaseId, username);
                return true;
            }
        } catch (final Exception e) {
            LogHelper.error(() -> "Error checking ownership for release ID {} and username {}: {}", releaseId, username,
                    e.getMessage(), e);
        }
        return false;
    }

    private static boolean isMatchingId(final int releaseId, final Long foundReleaseId) {
        return foundReleaseId != null && releaseId == foundReleaseId;
    }
}
