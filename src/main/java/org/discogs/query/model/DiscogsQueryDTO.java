package org.discogs.query.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import org.discogs.query.model.annotations.CompilationValidation;
import org.discogs.query.model.annotations.Normalized;
import org.discogs.query.model.annotations.VariousArtistsValidation;
import org.discogs.query.model.enums.DiscogCountries;
import org.discogs.query.model.enums.DiscogsTypes;

/**
 * Data Transfer Object (DTO) representing a query to search Discogs.
 * This class encapsulates the search criteria such as artist, track, and
 * optional format.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@CompilationValidation
@VariousArtistsValidation
public record DiscogsQueryDTO(
        /**
         * The name of the artist to search for. This field is required and
         * cannot be blank.
         */
        @NotBlank
        @Normalized
        String artist,

        /**
         * The name of the album to search for.
         */
        @Normalized
        String album,

        /**
         * The name of the track to search for.
         */
        @Normalized
        String track,

        /**
         * The format of the track (e.g., vinyl, CD, etc.). This field is optional.
         */
        String format,

        /**
         * The country for the Discogs entry.
         */
        DiscogCountries country,

        /**
         * Type of results we want to filter by.
         * Refer to {@link DiscogsTypes}.
         */
        DiscogsTypes types,

        /**
         * Barcode for the entry to search for.
         */
        String barcode
) {
}
