package org.discogs.query.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import org.discogs.query.model.annotations.CompilationValidation;
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
        @NotBlank
        String artist,

        String album,

        String track,

        String title,

        String format,

        DiscogCountries country,

        DiscogsTypes types,

        String barcode
) {
}
