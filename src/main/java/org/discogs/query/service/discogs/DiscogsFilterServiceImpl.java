package org.discogs.query.service.discogs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.api.DiscogsEntry;
import org.discogs.query.domain.api.DiscogsRelease;
import org.discogs.query.domain.api.DiscogsResult;
import org.discogs.query.domain.api.release.Artist;
import org.discogs.query.domain.api.release.Track;
import org.discogs.query.exceptions.DiscogsSearchException;
import org.discogs.query.helpers.DiscogsUrlBuilder;
import org.discogs.query.helpers.LogHelper;
import org.discogs.query.helpers.StringHelper;
import org.discogs.query.interfaces.DiscogsAPIClient;
import org.discogs.query.interfaces.DiscogsFilterService;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.service.NormalizationService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Implementation of {@link DiscogsFilterService} for filtering and sorting Discogs search results.
 * This service handles the logic for applying filters based on the artist and track name, as well
 * as sorting results by price.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiscogsFilterServiceImpl implements DiscogsFilterService {

    private final DiscogsAPIClient discogsAPIClient;
    private final DiscogsUrlBuilder discogsUrlBuilder;
    private final StringHelper stringHelper;
    private final NormalizationService normalizationService;

    /**
     * Checks if the track title from the query DTO matches or is contained in the given track title.
     *
     * @param discogsQueryDTO the search query data transfer object containing the track title.
     * @param track           the {@link Track} object containing the track title.
     * @return {@code true} if the track titles match or if the track title contains the query track
     * title, otherwise {@code false}.
     */
    private boolean isTrackEqualToOrContains(
            final DiscogsQueryDTO discogsQueryDTO, final Track track) {
        String title =
                normalizationService.normalizeString(track.getTitle().toLowerCase(Locale.ENGLISH));
        String normalizedQueryTrack = normalizationService.normalizeString(discogsQueryDTO.track());

        return title.equalsIgnoreCase(normalizedQueryTrack)
                || title.contains(normalizedQueryTrack.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Checks if the artist name from the query DTO matches the given artist name.
     *
     * @param discogsQueryDTO the search query data transfer object containing the artist name.
     * @param artistName      the artist name to compare with.
     * @return {@code true} if the artist names match, otherwise {@code false}.
     */
    private boolean isArtistNameMatching(
            final DiscogsQueryDTO discogsQueryDTO, final String artistName) {
        String normalizedArtistName = normalizationService.normalizeString(artistName);
        String normalizedDiscogsArtist = normalizationService.normalizeString(discogsQueryDTO.artist());

        return normalizedArtistName.equalsIgnoreCase(normalizedDiscogsArtist);
    }

    /**
     * Filters and sorts Discogs search results based on the provided query data. Filters out entries
     * without a price and sorts the remaining entries by lowest price.
     *
     * @param discogsQueryDTO the search query data transfer object containing filter criteria.
     * @param results         the Discogs search results to be filtered and sorted.
     */
    @Override
    public void filterAndSortResults(
            final DiscogsQueryDTO discogsQueryDTO, final DiscogsResult results) {
        LogHelper.info(() -> "Filtering and sorting results for query: {}", discogsQueryDTO);

        var filteredAndSortedResults =
                results.getResults().parallelStream()
                        .filter(entry -> filterIfTrackOnAlbum(entry, discogsQueryDTO))
                        .filter(entry -> Objects.nonNull(entry.getLowestPrice()))
                        .sorted((e1, e2) -> Float.compare(e1.getLowestPrice(), e2.getLowestPrice()))
                        .toList();

        LogHelper.info(
                () -> "Filtered and sorted {} results out of {}",
                filteredAndSortedResults.size(),
                results.getResults().size());
        results.setResults(filteredAndSortedResults);
    }

    /**
     * Retrieves detailed information about a Discogs release based on the provided Discogs entry.
     * Constructs the release URL and calls the Discogs API to fetch the release details.
     *
     * @param discogsEntry the Discogs entry containing the release ID.
     * @return the {@link DiscogsRelease} object containing detailed release information.
     * @throws DiscogsSearchException if an error occurs while retrieving the release details.
     */
    @Override
    public DiscogsRelease getReleaseDetails(final DiscogsEntry discogsEntry) {
        try {
            String releaseUrl = discogsUrlBuilder.buildReleaseUrl(discogsEntry);
            LogHelper.debug(() -> "Retrieving release details from URL: {}", releaseUrl);
            DiscogsRelease release = discogsAPIClient.getRelease(releaseUrl);
            LogHelper.info(() -> "Retrieved release details for entry ID {}", discogsEntry.getId());
            return release;
        } catch (final Exception e) {
            LogHelper.error(
                    () -> "Error retrieving release details for entry ID {}", discogsEntry.getId(), e);
            throw new DiscogsSearchException(
                    "Failed to retrieve release details for entry ID " + discogsEntry.getId(), e);
        }
    }

    /**
     * Filters out entries with null or zero lowest price from the given DiscogsResult.
     *
     * <p>This method updates the results in the given DiscogsResult object by removing any entries
     * where the lowest price is either null or zero. If the results list is null, it sets the results
     * to an empty list.
     *
     * @param results the DiscogsResult object containing a list of entries to be filtered
     */
    @Override
    public void filterOutEmptyLowestPrice(final DiscogsResult results) {
        if (results.getResults() == null) {
            results.setResults(Collections.emptyList());
            return;
        }

        var filteredResults =
                results.getResults().stream()
                        .filter(discogsEntry -> discogsEntry.getLowestPrice() != null)
                        .filter(discogsEntry -> discogsEntry.getLowestPrice() != 0f)
                        .toList();
        results.setResults(filteredResults);
    }

    /**
     * Filters a Discogs entry based on whether it contains the specified track on the album. This
     * method checks if the artist and track match the query criteria and whether the entry is not
     * categorized as "Various Artists" unless explicitly allowed.
     *
     * @param discogsEntry    the Discogs entry to be filtered.
     * @param discogsQueryDTO the search query data transfer object containing filter criteria.
     * @return {@code true} if the entry matches the query criteria, otherwise {@code false}.
     */
    private boolean filterIfTrackOnAlbum(
            final DiscogsEntry discogsEntry, final DiscogsQueryDTO discogsQueryDTO) {
        try {
            LogHelper.debug(() -> "Filtering track on album for entry ID {}", discogsEntry.getId());
            DiscogsRelease release = getReleaseDetails(discogsEntry);
            if (release == null) {
                LogHelper.error(() -> "No release details found for entry ID {}", discogsEntry.getId());
                return false;
            }

            boolean isOnAlbum =
                    !stringHelper.isNotVariousArtist(discogsQueryDTO.artist())
                            || filterArtists(discogsQueryDTO, release);

            if (stringHelper.isNotNullOrBlank(discogsQueryDTO.track())) {
                LogHelper.info(() -> "Track specified in query. Applying filter and sorting results...");
                isOnAlbum = filterTracks(discogsQueryDTO, release);
            }

            if (isOnAlbum) {
                LogHelper.debug(
                        () -> "Entry ID {} is on the album and matches the filters", discogsEntry.getId());
                discogsEntry.setLowestPrice((float) release.getLowestPrice());
            } else {
                LogHelper.debug(() -> "Entry ID {} does not match album filters", discogsEntry.getId());
            }
            return isOnAlbum;
        } catch (final Exception e) {
            LogHelper.error(
                    () -> "Error filtering track on album for entry ID {}", discogsEntry.getId(), e);
            return false;
        }
    }

    /**
     * Filters a Discogs release based on the artist name provided in the query DTO. Checks both
     * primary artists and extra artists for a match.
     *
     * @param discogsQueryDTO the search query data transfer object containing the artist name.
     * @param release         the {@link DiscogsRelease} object containing the release details.
     * @return {@code true} if the artist matches, otherwise {@code false}.
     */
    private boolean filterArtists(
            final DiscogsQueryDTO discogsQueryDTO, final DiscogsRelease release) {
        LogHelper.debug(() -> "Filtering artists for release ID {}", release.getId());
        boolean isArtistMatch = isArtistMatchNameOrAnyNameVariations(discogsQueryDTO, release);
        if (isArtistMatch) {
            return isArtistMatch;
        }
        if (release.getExtraArtists() != null) {
            LogHelper.debug(() -> "Checking extra artists for release ID {}", release.getId());
            isArtistMatch =
                    release.getExtraArtists().parallelStream()
                            .anyMatch(artist -> isArtistNameMatching(discogsQueryDTO, artist.getName()));
        }
        LogHelper.debug(
                () -> "Artist match status for release ID {}: {}", release.getId(), isArtistMatch);
        return isArtistMatch;
    }

    private boolean isArtistMatchNameOrAnyNameVariations(
            final DiscogsQueryDTO discogsQueryDTO, final DiscogsRelease release) {
        return release.getArtists().parallelStream()
                .anyMatch(
                        artist ->
                                isArtistNameMatching(discogsQueryDTO, artist.getName())
                                        || isArtistNameMatching(discogsQueryDTO, artist.getAnv()));
    }

    /**
     * Filters a Discogs release based on the track name provided in the query DTO. Checks if any
     * track on the release matches or contains the provided track name.
     *
     * @param discogsQueryDTO the search query data transfer object containing the track name.
     * @param release         the {@link DiscogsRelease} object containing the release details.
     * @return {@code true} if the track matches, otherwise {@code false}.
     */
    private boolean filterTracks(
            final DiscogsQueryDTO discogsQueryDTO, final DiscogsRelease release) {
        LogHelper.debug(() -> "Filtering tracks for release ID {}", release.getId());
        boolean trackMatch =
                release.getTracklist().parallelStream()
                        .anyMatch(track -> isTrackEqualToOrContains(discogsQueryDTO, track));
        if (trackMatch && isTrackListContainingArtists(release.getTracklist())) {
            trackMatch =
                    release.getTracklist().stream()
                            .map(Track::getArtists)
                            .filter(Objects::nonNull)
                            .anyMatch(artists -> isArtistInTrackList(artists, discogsQueryDTO));
        }
        LogHelper.debug(() -> "Track match status for release ID {}: {}", release.getId(), trackMatch);
        return trackMatch;
    }

    private boolean isTrackListContainingArtists(final List<Track> tracklist) {
        return !tracklist.parallelStream()
                .filter(track -> track.getArtists() != null)
                .toList()
                .isEmpty();
    }

    private boolean isArtistInTrackList(
            final List<Artist> artists, final DiscogsQueryDTO discogsQueryDTO) {
        for (final Artist artist : artists) {
            boolean match = isArtistNameMatching(discogsQueryDTO, artist.getName());
            if (match) {
                return true;
            }
            match = isArtistNameMatching(discogsQueryDTO, artist.getAnv());
            if (match) {
                return true;
            }
        }
        return false;
    }
}
