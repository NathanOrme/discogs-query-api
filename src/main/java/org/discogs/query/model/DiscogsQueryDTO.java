package org.discogs.query.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.discogs.query.annotations.VariousArtistsValidation;
import org.discogs.query.enums.DiscogCountries;
import org.discogs.query.enums.DiscogsTypes;

/**
 * Data Transfer Object (DTO) representing a query to search Discogs.
 * This class encapsulates the search criteria such as artist, track, and optional format.
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@VariousArtistsValidation
public class DiscogsQueryDTO {

    /**
     * The name of the artist to search for. This field is required and cannot be blank.
     */
    @NotBlank
    private String artist;

    /**
     * The name of the album to search for
     */
    private String album;

    /**
     * The name of the track to search for.
     */
    private String track;

    /**
     * The format of the track (e.g., vinyl, CD, etc.). This field is optional.
     */
    private String format;

    /**
     * The country for the Discogs entry
     */
    private DiscogCountries country;

    /**
     * Type of results we want to filter by.
     * Refer to {@link DiscogsTypes}
     */
    private DiscogsTypes types;

}