package org.discogs.query.domain.api;

import org.discogs.query.domain.api.release.Artist;
import org.discogs.query.domain.api.release.ExtraArtist;
import org.discogs.query.domain.api.release.Format;
import org.discogs.query.domain.api.release.Label;
import org.discogs.query.domain.api.release.Track;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DiscogsReleaseTest {

    @Test
    void testDefaultConstructor() {
        DiscogsRelease release = new DiscogsRelease();
        assertNull(release.getTitle());
        assertEquals(0, release.getId());
        assertNull(release.getArtists());
        assertNull(release.getExtraArtists());
        assertEquals(0, release.getFormatQuantity());
        assertNull(release.getFormats());
        assertNull(release.getGenres());
        assertNull(release.getLabels());
        assertEquals(0.0, release.getLowestPrice(), 0.01);
        assertEquals(0, release.getMasterId());
        assertNull(release.getMasterUrl());
        assertNull(release.getReleased());
        assertNull(release.getReleasedFormatted());
        assertNull(release.getResourceUrl());
        assertNull(release.getStatus());
        assertNull(release.getStyles());
        assertNull(release.getTracklist());
        assertNull(release.getUri());
        assertEquals(0, release.getYear());
    }

    @Test
    void testAllArgsConstructor() {
        List<Artist> artists = List.of(Artist.builder().name("Artist Name").build());
        List<ExtraArtist> extraArtists = List.of(ExtraArtist.builder().name("Extra Artist Name").build());
        List<Format> formats = List.of(Format.builder().name("Vinyl").build());
        List<String> genres = List.of("Rock", "Jazz");
        List<Label> labels = List.of(Label.builder().name("Label Name").build());
        List<Track> tracks = List.of(Track.builder().title("Track Title").build());

        DiscogsRelease release = new DiscogsRelease(
                "Release Title",
                12345,
                artists,
                extraArtists,
                2,
                formats,
                genres,
                labels,
                19.99,
                54321,
                "http://master-url",
                "2023-09-11",
                "September 11, 2023",
                "http://resource-url",
                "Official",
                List.of("Style 1", "Style 2"),
                tracks,
                "http://uri",
                2023
        );

        assertEquals("Release Title", release.getTitle());
        assertEquals(12345, release.getId());
        assertEquals(artists, release.getArtists());
        assertEquals(extraArtists, release.getExtraArtists());
        assertEquals(2, release.getFormatQuantity());
        assertEquals(formats, release.getFormats());
        assertEquals(genres, release.getGenres());
        assertEquals(labels, release.getLabels());
        assertEquals(19.99, release.getLowestPrice(), 0.01);
        assertEquals(54321, release.getMasterId());
        assertEquals("http://master-url", release.getMasterUrl());
        assertEquals("2023-09-11", release.getReleased());
        assertEquals("September 11, 2023", release.getReleasedFormatted());
        assertEquals("http://resource-url", release.getResourceUrl());
        assertEquals("Official", release.getStatus());
        assertEquals(List.of("Style 1", "Style 2"), release.getStyles());
        assertEquals(tracks, release.getTracklist());
        assertEquals("http://uri", release.getUri());
        assertEquals(2023, release.getYear());
    }

    @Test
    void testBuilder() {
        List<Artist> artists = List.of(Artist.builder().name("Artist Name").build());
        List<Format> formats = List.of(Format.builder().name("Vinyl").build());
        List<Track> tracks = List.of(Track.builder().title("Track Title").build());

        DiscogsRelease release = DiscogsRelease.builder()
                .title("Release Title")
                .id(12345)
                .artists(artists)
                .formats(formats)
                .tracklist(tracks)
                .lowestPrice(15.99)
                .year(2023)
                .build();

        assertEquals("Release Title", release.getTitle());
        assertEquals(12345, release.getId());
        assertEquals(artists, release.getArtists());
        assertEquals(formats, release.getFormats());
        assertEquals(tracks, release.getTracklist());
        assertEquals(15.99, release.getLowestPrice(), 0.01);
        assertEquals(2023, release.getYear());
    }

    @Test
    void testSetterAndGetter() {
        DiscogsRelease release = new DiscogsRelease();

        release.setTitle("Test Title");
        release.setId(54321);
        release.setLowestPrice(9.99);
        release.setYear(2022);

        assertEquals("Test Title", release.getTitle());
        assertEquals(54321, release.getId());
        assertEquals(9.99, release.getLowestPrice(), 0.01);
        assertEquals(2022, release.getYear());
    }

}

