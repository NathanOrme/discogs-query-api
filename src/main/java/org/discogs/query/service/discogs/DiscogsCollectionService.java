package org.discogs.query.service.discogs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.api.DiscogsRelease;
import org.discogs.query.helpers.DiscogsUrlBuilder;
import org.discogs.query.interfaces.DiscogsAPIClient;
import org.discogs.query.model.DiscogsEntryDTO;
import org.discogs.query.model.DiscogsResultDTO;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DiscogsCollectionService {

    private final DiscogsUrlBuilder discogsUrlBuilder;
    private final DiscogsAPIClient discogsAPIClient;

    /**
     * Method that filters out all results already owned by the username in their Discogs collection.
     *
     * @param username Username to search against.
     * @param entries  List of Discogs search results to filter, using their IDs.
     */
    public void filterOwnedReleases(final String username, final List<DiscogsResultDTO> entries) {
        if (entries == null || entries.isEmpty()) {
            log.warn("No entries provided for filtering.");
            return;
        }

        for (final DiscogsResultDTO entry : entries) {
            if (entry.results() == null || entry.results().isEmpty()) {
                log.warn("Skipping empty results for entry: {}", entry.searchQuery());
                continue;
            }

            // Filter out owned releases from the current entry's results
            List<DiscogsEntryDTO> filteredResults = entry.results().stream()
                    .filter(release -> !isReleaseOwnedByUser(username, release.id()))
                    .toList();

            // Update the results in-place with filtered results
            entries.set(entries.indexOf(entry), new DiscogsResultDTO(entry.searchQuery(), filteredResults));
        }
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
            DiscogsRelease ownedRelease = discogsAPIClient.getCollectionReleases(collectionUrl);

            if (ownedRelease != null && ownedRelease.getId() == releaseId) {
                log.info("Release ID {} is owned by user {}", releaseId, username);
                return true;
            }
        } catch (final Exception e) {
            log.error("Error checking ownership for release ID {} and username {}: {}", releaseId, username,
                    e.getMessage(), e);
        }
        return false;
    }
}
