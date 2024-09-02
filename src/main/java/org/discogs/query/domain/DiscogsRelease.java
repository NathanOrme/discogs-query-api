package org.discogs.query.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.discogs.query.domain.release.Artist;
import org.discogs.query.domain.release.ExtraArtist;
import org.discogs.query.domain.release.Format;
import org.discogs.query.domain.release.Label;
import org.discogs.query.domain.release.Track;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscogsRelease {
    private String title;
    private int id;
    private List<Artist> artists;
    @JsonProperty("extraartists")
    private List<ExtraArtist> extraArtists;
    @JsonProperty("format_quantity")
    private int formatQuantity;
    private List<Format> formats;
    private List<String> genres;
    private List<Label> labels;
    @JsonProperty("lowest_price")
    private double lowestPrice;
    @JsonProperty("master_id")
    private int masterId;
    @JsonProperty("master_url")
    private String masterUrl;
    private String released;
    @JsonProperty("released_formatted")
    private String releasedFormatted;
    @JsonProperty("resource_url")
    private String resourceUrl;
    private String status;
    private List<String> styles;
    private List<Track> tracklist;
    private String uri;
    private int year;
}


