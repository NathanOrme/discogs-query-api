package org.discogs.query.model.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.enums.DiscogsVarious;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Validator for ensuring that DiscogsQueryDTO is
 * properly validated when the artist is one of the "Various Artists".
 * This validator checks if the provided DTO meets
 * the necessary criteria based on the presence of certain fields.
 */
@Slf4j
@Component
public class VariousArtistsValidator
        implements ConstraintValidator<VariousArtistsValidation,
        DiscogsQueryDTO> {

    /**
     * Validates the provided DiscogsQueryDTO based on the context of
     * "Various Artists".
     * If the artist in the DTO matches one of the predefined "Various
     * Artists" values,
     * it checks whether the track or album fields are properly provided.
     *
     * @param discogsQueryDTO            the DiscogsQueryDTO object to be
     *                                   validated
     * @param constraintValidatorContext the context in which the constraint
     *                                   is evaluated
     * @return true if the DTO is valid or if
     * it does not match the criteria for "Various Artists"; false otherwise
     */
    @Override
    public boolean isValid(final DiscogsQueryDTO discogsQueryDTO,
                           final ConstraintValidatorContext constraintValidatorContext) {
        log.info("Starting validation for DiscogsQueryDTO for Various Artists" +
                " scenario.");

        if (discogsQueryDTO == null) {
            log.warn("DTO supplied to validator is null.");
            // Returning true here assumes that the null DTO is considered
            // valid.
            // Adjust based on your validation requirements.
            return true;
        }

        boolean isVariousArtist = Arrays.stream(DiscogsVarious.values())
                .map(DiscogsVarious::getVariousName)
                .anyMatch(value -> value.equalsIgnoreCase(discogsQueryDTO.getArtist()));

        if (isVariousArtist) {
            log.info("DTO artist is identified as a 'Various Artists' type.");
            boolean isTrackValid = isNotBlankBlank(discogsQueryDTO.getTrack());
            boolean isAlbumValid = isNotBlankBlank(discogsQueryDTO.getAlbum());
            if (isTrackValid) {
                log.info("Validation completed successfully.");
                return true;
            }
            if (isAlbumValid) {
                log.info("Validation completed successfully.");
                return true;
            }

            log.error("Invalid DiscogsQueryDTO: 'Various Artists' " +
                    "must be supplied with both a track and an album.");
            return false;
        }

        log.info("Validation completed successfully.");
        return true;
    }

    /**
     * Checks if a string is neither null nor blank.
     *
     * @param string the string to check
     * @return true if the string is non-null and non-blank; false otherwise
     */
    private boolean isNotBlankBlank(final String string) {
        return string != null && !string.isBlank();
    }
}
