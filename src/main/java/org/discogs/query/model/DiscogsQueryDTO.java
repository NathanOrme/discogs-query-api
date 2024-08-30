package org.discogs.query.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.discogs.query.enums.DiscogsTypes;

/**
 * Data Transfer Object (DTO) representing a query to search Discogs.
 * This class encapsulates the search criteria such as artist, track, and optional format.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscogsQueryDTO {

    /**
     * The name of the artist to search for. This field is required and cannot be blank.
     */
    @NotBlank
    private String artist;

    /**
     * The name of the track to search for. This field is required and cannot be blank.
     */
    @NotBlank
    private String track;

    /**
     * The format of the track (e.g., vinyl, CD, etc.). This field is optional.
     */
    private String format;

    /**
     * Type of results we want to filter by.
     * Refer to {@link DiscogsTypes}
     */
    private DiscogsTypes types;

}