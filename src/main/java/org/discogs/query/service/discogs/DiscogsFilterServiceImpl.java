package org.discogs.query.service.discogs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.domain.DiscogsEntry;
import org.discogs.query.domain.DiscogsRelease;
import org.discogs.query.domain.DiscogsResult;
import org.discogs.query.domain.release.Track;
import org.discogs.query.exceptions.DiscogsSearchException;
import org.discogs.query.helpers.DiscogsUrlBuilder;
import org.discogs.query.helpers.StringHelper;
import org.discogs.query.interfaces.DiscogsAPIClient;
import org.discogs.query.interfaces.DiscogsFilterService;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.service.NormalizationService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

/**
 * Implementation of {@link DiscogsFilterService} for filtering and sorting
 * Discogs search results.
 * This service handles the logic for applying filters based on the artist
 * and track name,
 * as well as sorting results by price.
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
     * Checks if the artist name from the query DTO matches the given artist
     * name.
     *
     * @param discogsQueryDTO the search query data transfer object
     *                        containing the artist name.
     * @param artistName      the artist name to compare with.
     * @return {@code true} if the artist names match, otherwise {@code false}.
     */
    private boolean isArtistNameMatching(final DiscogsQueryDTO discogsQueryDTO,
                                         final String artistName) {
        String normalizedArtistName = normalizationService.normalizeString(artistName);
        String normalizedDiscogsArtist = normalizationService.normalizeString(discogsQueryDTO.artist());

        return normalizedArtistName.equalsIgnoreCase(normalizedDiscogsArtist);
    }

    /**
     * Checks if the track title from the query DTO matches or is contained
     * in the given track title.
     *
     * @param discogsQueryDTO the search query data transfer object
     *                        containing the track title.
     * @param track           the {@link Track} object containing the track
     *                        title.
     * @return {@code true} if the track titles match or if the track title
     * contains the query track title, otherwise {@code false}.
     */
    private static boolean isTrackEqualToOrContains(final DiscogsQueryDTO discogsQueryDTO, final Track track) {
        String title = track.getTitle().toLowerCase();
        return title.equalsIgnoreCase(discogsQueryDTO.track())
                || title.contains(discogsQueryDTO.track().toLowerCase());
    }

    /**
     * Filters and sorts Discogs search results based on the provided query
     * data.
     * Filters out entries without a price and sorts the remaining entries by
     * lowest price.
     *
     * @param discogsQueryDTO the search query data transfer object
     *                        containing filter criteria.
     * @param results         the Discogs search results to be filtered and
     *                        sorted.
     */
    @Override
    public void filterAndSortResults(final DiscogsQueryDTO discogsQueryDTO,
                                     final DiscogsResult results) {
        log.info("Filtering and sorting results for query: {}",
                discogsQueryDTO);

        var filteredAndSortedResults = results.getResults().parallelStream()
                .filter(entry -> filterIfTrackOnAlbum(entry, discogsQueryDTO))
                .filter(entry -> Objects.nonNull(entry.getLowestPrice()))
                .sorted((e1, e2) -> Float.compare(e1.getLowestPrice(),
                        e2.getLowestPrice()))
                .toList();

        log.info("Filtered and sorted {} results out of {}",
                filteredAndSortedResults.size(), results.getResults().size());
        results.setResults(filteredAndSortedResults);
    }

    /**
     * Retrieves detailed information about a Discogs release based on the
     * provided Discogs entry.
     * Constructs the release URL and calls the Discogs API to fetch the
     * release details.
     *
     * @param discogsEntry the Discogs entry containing the release ID.
     * @return the {@link DiscogsRelease} object containing detailed release
     * information.
     * @throws DiscogsSearchException if an error occurs while retrieving the
     *                                release details.
     */
    @Override
    public DiscogsRelease getReleaseDetails(final DiscogsEntry discogsEntry) {
        try {
            String releaseUrl = discogsUrlBuilder.buildReleaseUrl(discogsEntry);
            log.debug("Retrieving release details from URL: {}", releaseUrl);
            DiscogsRelease release = discogsAPIClient.getRelease(releaseUrl);
            log.info("Retrieved release details for entry ID {}",
                    discogsEntry.getId());
            return release;
        } catch (final Exception e) {
            log.error("Error retrieving release details for entry ID {}",
                    discogsEntry.getId(), e);
            throw new DiscogsSearchException("Failed to retrieve release " +
                    "details for entry ID " +
                    discogsEntry.getId(), e);
        }
    }

    /**
     * Filters out entries with null or zero lowest price from the given DiscogsResult.
     *
     * <p>This method updates the results in the given DiscogsResult object by removing
     * any entries where the lowest price is either null or zero. If the results list is
     * null, it sets the results to an empty list.
     *
     * @param results the DiscogsResult object containing a list of entries to be filtered
     */
    @Override
    public void filterOutEmptyLowestPrice(final DiscogsResult results) {
        if (results.getResults() == null) {
            results.setResults(Collections.emptyList());
            return;
        }

        var filteredResults = results.getResults().stream()
                .filter(discogsEntry -> discogsEntry.getLowestPrice() != null)
                .filter(discogsEntry -> discogsEntry.getLowestPrice() != 0f)
                .toList();
        results.setResults(filteredResults);
    }


    /**
     * Filters a Discogs entry based on whether it contains the specified
     * track on the album.
     * This method checks if the artist and track match the query criteria
     * and whether the entry
     * is not categorized as "Various Artists" unless explicitly allowed.
     *
     * @param discogsEntry    the Discogs entry to be filtered.
     * @param discogsQueryDTO the search query data transfer object
     *                        containing filter criteria.
     * @return {@code true} if the entry matches the query criteria,
     * otherwise {@code false}.
     */
    private boolean filterIfTrackOnAlbum(final DiscogsEntry discogsEntry,
                                         final DiscogsQueryDTO discogsQueryDTO) {
        try {
            log.debug("Filtering track on album for entry ID {}",
                    discogsEntry.getId());
            DiscogsRelease release = getReleaseDetails(discogsEntry);
            if (release == null) {
                log.error("No release details found for entry ID {}",
                        discogsEntry.getId());
                return false;
            }
            boolean isOnAlbum = !stringHelper.isNotVariousArtist(discogsQueryDTO.artist())
                    || filterArtists(discogsQueryDTO, release);

            if (stringHelper.isNotNullOrBlank(discogsQueryDTO.track()) && isOnAlbum) {
                log.info("Track specified in query. Applying filter and " +
                        "sorting results...");
                isOnAlbum = filterTracks(discogsQueryDTO, release);
            }

            if (isOnAlbum) {
                log.debug("Entry ID {} is on the album and matches the " +
                        "filters", discogsEntry.getId());
                discogsEntry.setLowestPrice((float) release.getLowestPrice());
            } else {
                log.debug("Entry ID {} does not match album filters",
                        discogsEntry.getId());
            }
            return isOnAlbum;
        } catch (final Exception e) {
            log.error("Error filtering track on album for entry ID {}",
                    discogsEntry.getId(), e);
            return false;
        }
    }


    /**
     * Filters a Discogs release based on the artist name provided in the
     * query DTO.
     * Checks both primary artists and extra artists for a match.
     *
     * @param discogsQueryDTO the search query data transfer object
     *                        containing the artist name.
     * @param release         the {@link DiscogsRelease} object containing
     *                        the release details.
     * @return {@code true} if the artist matches, otherwise {@code false}.
     */
    private boolean filterArtists(final DiscogsQueryDTO discogsQueryDTO,
                                  final DiscogsRelease release) {
        log.debug("Filtering artists for release ID {}", release.getId());
        boolean isArtistMatch = release.getArtists().parallelStream()
                .anyMatch(artist -> isArtistNameMatching(discogsQueryDTO,
                        artist.getName()));

        if (!isArtistMatch && release.getExtraArtists() != null) {
            log.debug("Checking extra artists for release ID {}",
                    release.getId());
            isArtistMatch = release.getExtraArtists().parallelStream()
                    .anyMatch(artist -> isArtistNameMatching(discogsQueryDTO,
                            artist.getName()));
        }
        log.debug("Artist match status for release ID {}: {}",
                release.getId(), isArtistMatch);
        return isArtistMatch;
    }

    /**
     * Filters a Discogs release based on the track name provided in the
     * query DTO.
     * Checks if any track on the release matches or contains the provided
     * track name.
     *
     * @param discogsQueryDTO the search query data transfer object
     *                        containing the track name.
     * @param release         the {@link DiscogsRelease} object containing
     *                        the release details.
     * @return {@code true} if the track matches, otherwise {@code false}.
     */
    private boolean filterTracks(final DiscogsQueryDTO discogsQueryDTO,
                                 final DiscogsRelease release) {
        log.debug("Filtering tracks for release ID {}", release.getId());
        boolean trackMatch = release.getTracklist().parallelStream()
                .anyMatch(track -> isTrackEqualToOrContains(discogsQueryDTO,
                        track));
        log.debug("Track match status for release ID {}: {}", release.getId()
                , trackMatch);
        return trackMatch;
    }
}
