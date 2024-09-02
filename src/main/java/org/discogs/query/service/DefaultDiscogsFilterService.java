package org.discogs.query.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.client.DiscogsAPIClient;
import org.discogs.query.domain.DiscogsEntry;
import org.discogs.query.domain.DiscogsRelease;
import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.domain.release.Track;
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
        log.info("Filtering and sorting results for query: {}", discogsQueryDTO);

        var filteredAndSortedResults = results.getResults().stream()
                .filter(entry -> filterIfTrackOnAlbum(entry, discogsQueryDTO))
                .filter(entry -> {
                    boolean hasPrice = entry.getLowestPrice() != null;
                    if (!hasPrice) {
                        log.debug("Entry ID {} has no price, filtering out", entry.getId());
                    }
                    return hasPrice;
                })
                .sorted((e1, e2) -> Float.compare(e1.getLowestPrice(), e2.getLowestPrice()))
                .toList();

        log.info("Filtered and sorted {} results out of {}", filteredAndSortedResults.size(), results.getResults().size());
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
            log.debug("Retrieving release details from URL: {}", releaseUrl);
            DiscogsRelease release = discogsAPIClient.getRelease(releaseUrl);
            log.info("Retrieved release details for entry ID {}", discogsEntry.getId());
            return release;
        } catch (final Exception e) {
            log.error("Error retrieving release details for entry ID {}", discogsEntry.getId(), e);
            throw new DiscogsSearchException("Failed to retrieve release details", e);
        }
    }

    private boolean filterIfTrackOnAlbum(final DiscogsEntry discogsEntry, final DiscogsQueryDTO discogsQueryDTO) {
        try {
            log.debug("Filtering track on album for entry ID {}", discogsEntry.getId());
            DiscogsRelease release = getReleaseDetails(discogsEntry);
            if (release == null) {
                log.error("No release details found for entry ID {}", discogsEntry.getId());
                return false;
            }
            boolean isOnAlbum = filterArtists(discogsQueryDTO, release);
            if (isOnAlbum) {
                isOnAlbum = filterTracks(discogsQueryDTO, release);
            }
            if (isOnAlbum) {
                log.debug("Entry ID {} is on the album and matches the filters", discogsEntry.getId());
                discogsEntry.setLowestPrice((float) release.getLowestPrice());
            } else {
                log.debug("Entry ID {} does not match album filters", discogsEntry.getId());
            }
            return isOnAlbum;
        } catch (final Exception e) {
            log.error("Error filtering track on album for entry ID {}", discogsEntry.getId(), e);
            return false;
        }
    }

    private boolean filterArtists(final DiscogsQueryDTO discogsQueryDTO, final DiscogsRelease release) {
        log.debug("Filtering artists for release ID {}", release.getId());
        boolean isArtistMatch = release.getArtists().stream()
                .anyMatch(artist -> isArtistNameMatching(discogsQueryDTO, artist.getName()));
        if (!isArtistMatch && release.getExtraArtists() != null) {
            log.debug("Checking extra artists for release ID {}", release.getId());
            isArtistMatch = release.getExtraArtists().stream()
                    .anyMatch(artist -> isArtistNameMatching(discogsQueryDTO, artist.getName()));
        }
        log.debug("Artist match status for release ID {}: {}", release.getId(), isArtistMatch);
        return isArtistMatch;
    }

    private static boolean isArtistNameMatching(final DiscogsQueryDTO discogsQueryDTO, final String artistName) {
        return artistName.equalsIgnoreCase(discogsQueryDTO.getArtist());
    }

    private boolean filterTracks(final DiscogsQueryDTO discogsQueryDTO, final DiscogsRelease release) {
        log.debug("Filtering tracks for release ID {}", release.getId());
        boolean trackMatch = release.getTracklist().stream()
                .anyMatch(track -> isTrackEqualToOrContains(discogsQueryDTO, track));
        log.debug("Track match status for release ID {}: {}", release.getId(), trackMatch);
        return trackMatch;
    }

    private static boolean isTrackEqualToOrContains(final DiscogsQueryDTO discogsQueryDTO, final Track track) {
        String title = track.getTitle().toLowerCase();
        return title.equalsIgnoreCase(discogsQueryDTO.getTrack())
                || title.contains(discogsQueryDTO.getTrack().toLowerCase());
    }
}
