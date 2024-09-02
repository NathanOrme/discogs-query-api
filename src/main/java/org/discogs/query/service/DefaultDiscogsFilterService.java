package org.discogs.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.client.DiscogsAPIClient;
import org.discogs.query.domain.DiscogsEntry;
import org.discogs.query.domain.DiscogsRelease;
import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.exceptions.DiscogsSearchException;
import org.discogs.query.helpers.DiscogsUrlBuilder;
import org.discogs.query.interfaces.DiscogsFilterService;
import org.discogs.query.model.DiscogsQueryDTO;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link DiscogsFilterService} for filtering and sorting Discogs search results.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultDiscogsFilterService implements DiscogsFilterService {

    private final DiscogsAPIClient discogsAPIClient;
    private final DiscogsUrlBuilder discogsUrlBuilder;

    /**
     * Filters and sorts the search results based on the query DTO.
     *
     * @param discogsQueryDTO the search query data transfer object
     * @param results         the search results to be filtered and sorted
     */
    @Override
    public void filterAndSortResults(final DiscogsQueryDTO discogsQueryDTO, final DiscogsResult results) {
        var filteredAndSortedResults = results.getResults().stream()
                .filter(entry -> filterIfTrackOnAlbum(entry, discogsQueryDTO))
                .filter(entry -> entry.getLowestPrice() != null)
                .sorted((e1, e2) -> Float.compare(e1.getLowestPrice(), e2.getLowestPrice()))
                .toList();
        results.setResults(filteredAndSortedResults);
    }

    /**
     * Retrieves a {@link DiscogsRelease} object based on the provided {@link DiscogsEntry}.
     *
     * @param discogsEntry the Discogs entry containing the release ID
     * @return the {@link DiscogsRelease} object
     */
    @Override
    public DiscogsRelease getReleaseDetails(final DiscogsEntry discogsEntry) {
        try {
            String releaseUrl = discogsUrlBuilder.buildReleaseUrl(discogsEntry);
            return discogsAPIClient.getRelease(releaseUrl);
        } catch (final Exception e) {
            log.error("Error retrieving release details for entry ID {}", discogsEntry.getId(), e);
            throw new DiscogsSearchException("Failed to retrieve release details", e);
        }
    }

    private boolean filterIfTrackOnAlbum(final DiscogsEntry discogsEntry, final DiscogsQueryDTO discogsQueryDTO) {
        try {
            DiscogsRelease release = getReleaseDetails(discogsEntry);
            boolean isOnAlbum = filterArtists(discogsQueryDTO, release);
            if (isOnAlbum) {
                isOnAlbum = filterTracks(discogsQueryDTO, release);
            }
            if (isOnAlbum) {
                discogsEntry.setLowestPrice((float) release.getLowestPrice());
            }
            return isOnAlbum;
        } catch (final Exception e) {
            log.error("Error filtering track on album", e);
            return false;
        }
    }

    private boolean filterArtists(final DiscogsQueryDTO discogsQueryDTO, final DiscogsRelease release) {
        return release.getArtists().stream()
                .anyMatch(artist -> artist.getName().equalsIgnoreCase(discogsQueryDTO.getArtist())) ||
                release.getExtraArtists().stream()
                        .anyMatch(artist -> artist.getName().equalsIgnoreCase(discogsQueryDTO.getArtist()));
    }

    private boolean filterTracks(final DiscogsQueryDTO discogsQueryDTO, final DiscogsRelease release) {
        return release.getTracklist().stream()
                .anyMatch(track -> track.getTitle().equalsIgnoreCase(discogsQueryDTO.getTrack()));
    }
}
